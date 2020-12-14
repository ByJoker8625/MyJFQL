# MyJFQL DMBS

MyJFQL is an open source database management system for short dmbs developed by [JokerGames](http://jokergames.ddnss.de)
. With this DMBS you can manage your database. It runs on all operating systems on which Java 11 is installed. MyJFQL
uses [JFQL](http://jokergames.ddnss.de/documentation/) as a language and not like most other SQL. JFQL is similar to SQL
but is very simplified.

### Features:

* MySQL is very fast and resource saving as it was written in java
* You can write modules for MySQL with which you can completely customize the DBMS
* There is support for many languages such as JavaScript, Java and Python
* If your language is not directly supported, you can simply write your own connector with an HTTP client and Json.

### Start:

To start MySQL you need to have java 11 installed.

```bash
java -Xmx2G -Xms1G -jar MyJFQL.jar
```
