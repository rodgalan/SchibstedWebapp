# SchibstedWebapp
Implement a Web Application using the Java language (com.sun.httpserver)

# Technical requirements
Build : 	
- JDK  1.8 or higher
- Maven
	
Execution : 
- JRE/JDK 1.8 or higher
 

# Getting project code
Get sources from from GitHub Project: https://github.com/rodgalan/SchibstedWebapp

# Generating executable jar
mvn package --> Generates executable jar into target project folder (webapp-0.0.1-SNAPSHOT.jar)

# Running the server
Server can be started on default port (8080):
	java -jar webapp-0.0.1-SNAPSHOT.jar

Or on a specific another port
	java -jar webapp-0.0.1-SNAPSHOT.jar {$port}

#Default users
4 users have been created for testing purposes. These users can be used in both, Web application and REST services. 

	- Username: admin, password: admin, Rols: ADMIN
	- Username: user1, password: user1, Rols: PAGE1
	- Username: user2, password: user2, Rols: PAGE2
	- Username: user3, password: user3, Rols: PAGE3



#Web application
WebApp Context: /webapp
	
	- Login page: 			/login 		(No authentication required)
	- Page 1: 			/page1 		(Page under authentication and authorization PAGE1)
	- Page 2: 			/page2		(Page under authentication and authorization PAGE2)
	- Page 3: 			/page3		(Page under authentication and authorization PAGE3)


#User service
No contract specification provided (not SAGGER, not WADL) at this version.   

	Security Specification

User Service security policy is basic Http authentication (same users than web application)
Authorization is not implemented yet. Next release must restrict access to resources by applying authorization policies.

	Service resources:  
	
Base Service path: /service/users
	
	- /service/users 		+ GET :		Get Users lists
	- /service/users/{$userId} 	+ GET: 		Get {$userId} detail
	- /service/users/{$userId} 	+ DELETE:	Delete user {$userId}
	- /service/users 		+ PUT: 		Add new user
	- /service/users/{$userId} 	+ POST: 	Modifies {$userId} data and rols 
	
Service messages definition (at this version only XML, and afirst non tested version of JSON service GET + /service/users/{$userId}

User entity (XML representation example):
	
		<userInfo>
		  <username>username</username>
		  <rolsInfo>
		    <rol>ADMIN/ROL1/ROL2/ROL3</rol>
		    <rol>ADMIN/ROL1/ROL2/ROL3</rol>
		  </rolsInfo>
		</userInfo>	
		
# Local enviroment used for developement and testing
- Eclipse Mars.1 Release (4.5.1)
- Project encoding UTF-8
- Apache Maven 3.3.9 
- Java version: 1.8.0_51, vendor: Oracle Corporation
- Default locale: es_ES, platform encoding: Cp1252
- OS name: "windows 7", version: "6.1", arch: "amd64", family: "dos

#Identified bugs (pending to fix)
- Multithreading problems

#Design
- Controller : custom framewrok developed to configure flow navigation in XML file. Each user reuquest is delegated to appropiate business action. It manages the authorization acces to resources.
- Model: Simple beans (Pojo)
- Views: After a busniess action is finisehd, controller delegates the creation of view to View component. Views can be static (html) or dynamic (java generating html code), or simply a redirect location.
- ViewBeans: Object containing data to pass information between business action and view.
- DAO: Decoupled form data origin with an interface defining methods that must be implemented by every DAO implemenetation. At this release there aren't a Factory provided (to improve in next releases)
- Data Storagge: Data are not persisted for this PoC. All user data is cached in a singleton. To simulate physical storage and ensures values are not modified directly (always must be done by dao layer), singleton returns cloned objects.
- HTTP Session for webapp: provided by client side cookie and saved at server side in a  singleton.
- Handlers and filters: Webapp and User service has it's own handler and filter. Handler recieives all user requests that has not been discared previously by filter. 
- Http Basic authorization for REST service is provider by an Authenticator.
- REST services: two custom annotations have been developed. Service handler delegates to appropiate service executor annotated with relative uri (and query string) and http method. (*) Actually there a bug and a woraround is provided (switch condition in handler).



