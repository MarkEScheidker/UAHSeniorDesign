# UAH Senior Design

# What is this?
This is the code repository for our senior design project, which will 
include the Kotlin code for both a website, and the backend server code 
that makes it run.

# What does it do?
At the moment, we have set up gradle to compile the front and backend 
compponents of a kvision/Ktor server, and sends it to a physical server box 
running on a home network. Web pages and the backend components that host 
the services that make them are currently in development, and mostly completed.
The backend is fully integrated with the mysql database, and has several functions
that allow for the access and modification of data.

# What will the final product look like?
The project is already feature complete, but needs more polish and some reworking of existing features to improve performance, usability, and security. Customers with a UAH email are able to sign up and log in securely, as well as change passwords and edit account information. Customers are able to order food from the dynamically generated menus read from the database, and send those orders to the backend, which then sends the order to the correct restaurant account orders queue page. Restaurants are able to log in and view orders, checking them off of the list when they are complete. SMS messaging is implemented with Twillio's API and enables direct messaging of a customer's device to enable realtime order status tracking. Everything except actual money handling/calculating is done correctly, as handling secure payments is beyond the scope of this project, and unsafe at this time. Wishlist items that need to be added are storage of order queues on the database to prevent loss of order queue in the event of a server reset, and minor improvements of the frontend ui to give the website more visual polish.

# Database Schema
![ChargerFuelDatabaseSchema](https://user-images.githubusercontent.com/83242911/234393810-89e298bd-2950-4533-90d4-9440b1bf1768.PNG)

We used a stardard MySQL Database. All the lines represent forgein keys. You will need to change the backend to link to your database, with its user and password. We made a seperate login that had less permissions to increase security.
