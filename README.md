# Cyber Security Base with F-Secure, Course Project I
This document is highly recommended to be read properly rendered at the project's GitHub [page](https://github.com/ClericApton/oops.git).

#### Description of the application
People have username/password combinations for various online services such as webmail, online stores, gaming platforms, etc. Using this web application a user can save and retrieve her service/username/password combinations. After logging in a user can enter this data and the application will store it in a simple SQLite database. A user can retrieve her stored data by using the included search-by-service-name feature. Finally the application offers a deletion feature for removing from the database all the data the user has entered.

The application has three regular users and an administrator whose username/password combinations (hardcoded in SecurityConfig.java) are as follows: Alice/alice, Bob/bob, Mallory/mallory, admin/admin. When logged in, an administrator can enter messages, the latest of which will be shown to the regular users. These messages are stored in the database as well.

#### Installing and running the application
1. Clone the GitHub repository using the command prompt: `git clone https://github.com/ClericApton/oops.git`
2. Change current directory to the project's root directory: `cd oops`
3. Run the application: `mvn spring-boot:run`

#### The vulnerabilities
The application has various security vulnerabilities. They are described and discussed next, as well as methods of fixing them. The issues are categorized as in [OWASP](https://www.owasp.org/index.php/Top_10_2013-Top_10). The user Mallory is considered malicious in the following discussion.

1. SQL injection revealing sensitive data (Category A1 Injection)
    1. Open http://localhost:8080/login in a web browser.
    2. Enter Mallory/mallory as Username/Password and press the Log in button.
    3. Enter the following in the Service field: `' OR 1=1 OR '` (starting and ending with ') and submit.
    4. All the service/username/password entries in the database are revealed.

    In EntryDao.java the query for selecting entries is formed by simply concatenating character strings which permits SQL injection. In this case the query will become `SELECT * FROM entry WHERE user = 'Mallory' AND service LIKE '%' OR 1=1 OR '%' ORDER BY service;`. The condition `1=1` is always true so the query will return all the rows in the table. This issue can be fixed by using parameterized queries instead of concatenation.

2. Broken log out handling (Category A2 Broken Authentication and Session Management)
    1. Open http://localhost:8080/login in a web browser.
    2. Enter Alice/alice as Username/Password and press the Log in button.
    3. Press the Log out button.
    4. Open http://localhost:8080/user in the browser.
    5. Possible unauthorized access to the user's page is obtained.

    Proper log out handling is disabled in SecurityConfig.java (line 37). Therefore pressing either of the two log out buttons (in user.html or admin.html) simply redirects to /login without clearing authentication data on the server so the user remains logged in as far as the server is concerned. This can be a problem if the application has been used on a public computer. The issue can be fixed by enabling log out handling, i.e. removing `.logout().disable()` from SecurityConfig.java.

3. Sensitive data sent over unencrypted connections (Category A6 Sensitive Data Exposure)
    1. Open http://localhost:8080/login in a web browser.
    2. Notice the browser's address bar that the connection is not secure.
    3. Enter Alice/alice as Username/Password and press the Log in button.
    4. Repeat Step 2.

    All data between the user's computer and the server at each interaction stage is transmitted unencrypted so an attacker can gain access to the credentials, session tokens and the sensitive data sent while the user is logged in by simply monitoring network traffic. The issue can be fixed by requiring that only connections over HTTPS are allowed. This is achieved by getting a public key certificate (for example self-signed) and making needed configurations in application.properties under src/main/resources.

4. The administrator's password is not secure (Category A5 Security Misconfiguration)
    1. Open http://localhost:8080/login in a web browser.
    2. Enter admin/admin as Username/Password and press the Log in button.
    3. Possible unauthorized access to the administrator's page is obtained.

    The default username/password combination admin/admin is left enabled. The attacker can easily guess or use brute-force attack to gain access to the administrator's page. This can be fixed by changing the administrator's password to a more secure one.

5. Regular user can access the administrator's page (Category A4 Insecure Direct Object References)
    1. Log in as Mallory (as in Issue 1 Steps 1 and 2).
    2. Open http://localhost:8080/admin in the web browser.
    3. Unauthorized access to the administrator's page is obtained.

    Authorization is not configured correctly in SecurityConfig.java. Any authenticated user can access the administrator's page. This can be fixed by giving access to the page only to principals with the role ADMIN by adding `.antMatchers("/admin").hasRole("ADMIN")` in SecurityConfig.java.

6. Deleting data by forging a request (Category A8 Cross-Site Request Forgery (CSRF))
    1. Log in as Alice (as in Issue 2 Steps 1 and 2).
    2. Press the first Submit button.
    3. Notice Alice's data.
    4. On a new tab of the same browser, open csrf.html located at the root of the project.
    5. Return to the first tab and press the first Submit button again.
    6. Notice that the data has been deleted.

    Deletion is triggered when an authenticated user's browser sends a GET request to the address /delete. This request can be forged by the user visiting a web page where the address has been set (for example) as an image source as in csrf.html. The issue arises as a result of a violation of the principle that a GET request should not result in a state change on the server. Instead a POST or DELETE request should be used. Anti-CSRF techniques are enabled on the server. Namely, the forms that send a POST request have hidden CSRF tokens (see the source of a user's page in the browser for example). But it's of no use since the token is not present when a form sends a GET request as the one with the delete button. Fixing this requires changing the request methods of the forms with the log out buttons in the templates user.html and admin.html to POST.

7. Scripts embedded in messages are executed (Category A3 Cross-Site Scripting (XSS))
    1. Log in as Mallory and open the administrator's page (as in Issue 5 Steps 1-3).
    2. Enter `<script type="text/javascript">alert("This is Mallory's malicious script!");</script>` in the message field and submit.
    3. Press the Log out button.
    4. Enter Alice/alice as Username/Password and press the Log in button.
    5. Notice that a potentially malicious script is executed.

    Because of the `th:utext tag` (line 12) in user.html the template engine leaves administrator's messages unescaped which results in the browser executing any scripts embedded in the messages. A malicious script could send the content of the current page to a remote server controller by the attacker for example. This vulnerability is fixed simply by changing the tag `th:utext` to `th:text` (as in admin.html) which instructs Thymeleaf to escape the message's content.

These seven issues are fixed in the branch fixed. Please note that to reach the fixed application's log in page you must open https://localhost:8443/login.

Thank you for reading and testing the application.
