# UAH Senior Design

# What is this?
This is the code repository for our senior design project, which will 
include the Kotlin code for both a website, and the backend server code 
that makes it run.

# What does it do?
At the moment, we have set up gradle to compile the front and backend 
compponents of a kvision/Ktor server, and sends it to a physical server box 
running on a home network. Web pages and the backend components that host 
the services that make them are currently in development, and a Mysql database 
integration is currently underway.  

# What will the final product look like?
We plan to allow new users to create accounts with an email and 
password. Passwords should be stored securely using hashing, and passwords 
should be reset through email verification. Users should be able to order 
food at a specific restaurant and "pay" with a credit card, which will 
also be handled securely. Restaurant specific accounts can also be created, 
and allow restaurants to see customer's orders in a queue, as well as mark 
when specific orders are complete, alerting the user to the status of their order. Security will be one of the top priorities, and customer data will be handled safely, and the database should be protected from SQL injections as well as other conventional attacks. All of this will be handled on a single server, and hosted on a website for simplicity.

