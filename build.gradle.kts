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

    private fun Session.uploadZip() {
        (openChannel("sftp") as ChannelSftp).run {
            connect()
            cd("/opt/charger_fuel")
            put(FileInputStream(File("./build/libs/chargerfuel.zip")),"chargerfuel.zip")
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
                if (isClosed) {
                    println("exit-status: $exitStatus")
                    break;
                }
                Thread.sleep(1000)
            }
            disconnect()
        }
    }

    override fun apply(target: Project) {
        target.task("upload") {
            group = "charger fuel"
            dependsOn("clean", "zip")
            doLast {
                establishSession()?.run {
                    println("Removing old files...")
                    runCommand("rm /opt/charger_fuel/*")
                    println("Uploading zip...")
                    uploadZip()
                    println("Unpacking zip...")
                    runCommand("cd /opt/charger_fuel/ && unzip -o /opt/charger_fuel/chargerfuel.zip && rm chargerfuel.zip")
                    println("Reloading server...")
                    runCommand("nginx -s reload")
                    disconnect()
                }
            }
        }
    }
}

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("js") version kotlinVersion
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

val webDir = file("src/main/web")

kotlin {
    js {
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
                    static = mutableListOf("$buildDir/processedResources/js/main")
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
    sourceSets["main"].dependencies {
        implementation("io.kvision:kvision:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
        implementation("io.kvision:kvision-bootstrap-css:$kvisionVersion")
    }
    sourceSets["test"].dependencies {
        implementation(kotlin("test-js"))
        implementation("io.kvision:kvision-testutils:$kvisionVersion")
    }
    sourceSets["main"].resources.srcDir(webDir)
}
