# Non-functional requirements

Fault tolerance
 - the application should be resiliant to errors caused by server-side disconnect (gracefuly show the error to the user)
 - the application should be resiliant to errors caused by database connection errors (gracefuly show the error to the user)

Open source
 - the application and all of its components should be open-source

Internationalization and localization
 - the application should support timezones

Backup:
 - all data kept at one place (one-file database) to facilitate easy backup creation 

Performance 
 - the application opens in less than 10s
 - adding/deleting events takes no more than 3s

Platform portability
 - the application should run on, at least: Windows 10 and Ubuntu Linux
 - the development environment should be available on at least: Windows 10 and Ubuntu Linux

Data portability 
 - the user should be able to move the data out of the application easy (soft requirement)
 - this means using an established, open-source data storage format

Ergonomy
 - every main action in the program (calendar view, adding events, adding tasks) should be accessible from the main page
 - the application should look the same on all platforms

Volume
 - the application should be able to handle large volume of user-generated data
 - this means no errors handling at least 100 events for 1000 days

Interoperability
 - the application should support interoperability with other calendar software (using open format iCal)