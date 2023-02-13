import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.io.FileInputStream
import java.util.*

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        gradleApi()
        classpath("com.github.mwiede:jsch:0.2.6")
    }
}

class ChargerPlugin : Plugin<Project> {
    private fun establishSession() = JSch()
        .apply { addIdentity("~/.ssh/charger") }
        .getSession("jetson", "charger.food.is")
        ?.apply {
            setConfig(Properties().apply { put("StrictHostKeyChecking", "no") })
            connect()
        }

    private fun Session.uploadJar() {
        (openChannel("sftp") as ChannelSftp).run {
            connect()
            cd("/opt/charger_fuel")
            put(FileInputStream(File("./build/libs/chargerfuel.jar")), "chargerfuel.jar")
            disconnect()
        }
    }

    private fun Session.uploadNginxConfig() {
        (openChannel("sftp") as ChannelSftp).run {
            connect()
            cd("/etc/nginx/sites-available")
            put(FileInputStream(File("./nginx/charger.food.is")), "charger.food.is")
            disconnect()
        }
    }

    private fun Session.runCommand(cmd: String) {
        (openChannel("exec") as ChannelExec).run {
            setCommand(cmd)
            setPty(true)

            val inStream = inputStream
            val outStream = outputStream
            setErrStream(System.err)
            connect()
            val read = ByteArray(1024)
            while (true) {
                while (inStream.available() > 0) {
                    val i = inStream.read(read, 0, 1024)
                    if (i < 0) break;
                    println(String(read, 0, i))
                }
                if (isClosed) break;
                Thread.sleep(1000)
            }
            disconnect()
        }
    }

    override fun apply(target: Project) {
        target.task("upload") {
            group = "charger fuel"
            dependsOn("jar")
            doLast {
                establishSession()?.run {
                    println("Killing old server...")
                    runCommand("pkill -f \"chargerfuel -jar /opt/charger_fuel/chargerfuel.jar\"")
                    println("Removing old jar...")
                    runCommand("rm /opt/charger_fuel/chargerfuel.jar")
                    println("Uploading jar...")
                    uploadJar()
                    println("Reloading server...")
                    runCommand("nohup bash -c 'exec -a \"chargerfuel\" java -jar /opt/charger_fuel/chargerfuel.jar > /opt/charger_fuel/log.txt 2>&1 &'")
                    println("all tasks complete")
                    disconnect()
                }
            }
        }
        target.task("nginx") {
            group = "charger fuel"
            doLast {
                establishSession()?.run {
                    println("uploading nginx config...")
                    uploadNginxConfig()
                    println("reloading nginx...")
                    runCommand("sudo /usr/sbin/service nginx reload")
                    println("all tasks complete")
                }
            }
        }
    }
}

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("multiplatform") version kotlinVersion
    val kvisionVersion: String by System.getProperties()
    id("io.kvision") version kvisionVersion
}

apply<ChargerPlugin>()

version = ""
group = "com.chargerfuel"

repositories {
    mavenCentral()
    mavenLocal()
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()
val ktorVersion: String by project
val logbackVersion: String by project

val webDir = file("src/frontendMain/web")
val mainClassName = "io.ktor.server.netty.EngineMain"

kotlin {
    jvm("backend") {
        compilations.all {
            java {
                targetCompatibility = JavaVersion.VERSION_1_8
            }
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
    }
    js("frontend") {
        browser {
            runTask {
                outputFileName = "main.bundle.js"
                sourceMaps = false
                devServer = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 3000,
                    proxy = mutableMapOf(
                        "/kv/*" to "http://localhost:8080",
                        "/kvws/*" to mapOf("target" to "ws://localhost:8080", "ws" to true)
                    ),
                    static = mutableListOf("$buildDir/processedResources/frontend/main")
                )
            }
            webpackTask {
                outputFileName = "main.bundle.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.kvision:kvision-server-ktor-koin:$kvisionVersion")
            }
            kotlin.srcDir("build/generated-src/common")
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val backendMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
                implementation("io.ktor:ktor-server-compression:$ktorVersion")
                implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("mysql:mysql-connector-java:8.0.15")
                implementation("org.mindrot:jbcrypt:0.4")
                implementation("com.sun.mail:javax.mail:1.6.2")

            }
        }
        val backendTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val frontendMain by getting {
            resources.srcDir(webDir)
            dependencies {
                implementation("io.kvision:kvision:$kvisionVersion")
                implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
                implementation("io.kvision:kvision-bootstrap-css:$kvisionVersion")
            }
            kotlin.srcDir("build/generated-src/frontend")
        }
        val frontendTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("io.kvision:kvision-testutils:$kvisionVersion")
            }
        }
    }
}

afterEvaluate {
    tasks {
        create("frontendArchive", Jar::class).apply {
            dependsOn("frontendBrowserProductionWebpack")
            group = "package"
            archiveAppendix.set("frontend")
            val distribution =
                project.tasks.getByName(
                    "frontendBrowserProductionWebpack",
                    org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack::class
                ).destinationDirectory!!
            from(distribution) {
                include("*.*")
            }
            from(webDir)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            into("/assets")
            inputs.files(distribution, webDir)
            outputs.file(archiveFile)
            manifest {
                attributes(
                    mapOf(
                        "Implementation-Title" to rootProject.name,
                        "Implementation-Group" to rootProject.group,
                        "Implementation-Version" to rootProject.version,
                        "Timestamp" to System.currentTimeMillis()
                    )
                )
            }
        }
        getByName("backendProcessResources", Copy::class) {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        getByName("backendJar").group = "package"
        create("jar", Jar::class).apply {
            dependsOn("frontendArchive", "backendJar")
            group = "package"
            manifest {
                attributes(
                    mapOf(
                        "Implementation-Title" to rootProject.name,
                        "Implementation-Group" to rootProject.group,
                        "Implementation-Version" to rootProject.version,
                        "Timestamp" to System.currentTimeMillis(),
                        "Main-Class" to mainClassName
                    )
                )
            }
            val dependencies = configurations["backendRuntimeClasspath"].filter { it.name.endsWith(".jar") } +
                    project.tasks["backendJar"].outputs.files +
                    project.tasks["frontendArchive"].outputs.files
            dependencies.forEach {
                if (it.isDirectory) from(it) else from(zipTree(it))
            }
            exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
            inputs.files(dependencies)
            outputs.file(archiveFile)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        create("backendRun", JavaExec::class) {
            dependsOn("compileKotlinBackend")
            group = "run"
            main = mainClassName
            classpath =
                configurations["backendRuntimeClasspath"] + project.tasks["compileKotlinBackend"].outputs.files +
                        project.tasks["backendProcessResources"].outputs.files
            workingDir = buildDir
        }
    }
}
