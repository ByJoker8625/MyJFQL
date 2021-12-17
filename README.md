# MyJFQL

## Introduction

MyJFQL is an open source database management system for short dmbs. With this DMBS you can manage your database easily.
It runs on **all operating systems** on which Java 8 is installed. MyJFQL uses JFQL as a language and not like most
other SQL. JFQL is similar to SQL but is **very simplified**. The syntax is much easier to use than SQL and there are no
commas or brackets. Because of this, JFQL is extremely easy to learn and **beginner-friendly**.

## Documentation

This documentation applies for version ``1.5.2``. Not everything mentioned works with older versions so be careful.

### Installation

***

#### Download

If you want to download MyJFQL you have to make sure that Java 8 or higher is installed. Then you can click [download]()
to download the jar. You can run the jar like this

#### Starting

```shell
java -jar MyJFQL.jar
```

If you want to give MyJFQL a specific ram amount you have to add ``-Xmx${AMOUNT}`` and ``-Xms${AMOUNT}``.

#### Linux script

With linux you can use screen to create a seperated screen of MyJFQL. Simply create a ``start.sh`` with this content

```shell
screen -AmdS MyJFQL java -jar MyJFQL.jar
```

The ``start.sh`` file only needs permissions to be able to run

```shell
chmod 777 start.sh
```

Then you can run the script with entering

```shell
./start.sh
```

### Connection

***

#### Open session

Send a **POST** request to ``http://${server}:${port}/api/v1/session/open`` with a login request. Then you get back a
session as response. If you want directly connect with an token enter ``%TOKEN%`` as username and your token as
password.

##### LoginRequest structure

| Field    |  Type  | Description                        |
|:---------|:------:|------------------------------------|
| user     | string | the user identifier *(name or id)* |
| password | string | the password of the user           |

##### LoginRequest example

```json
{
  "user": "root",
  "password": "pw"
}
```

#### Querying

If you want to query to MyJFQL you have to make a **POST** request to ``http://${server}:${port}/api/v1/query`` with a
query request.

##### QueryRequest structure

| Field |  Type  | Description                          |
|:------|:------:|--------------------------------------|
| token | string | the session token                    |
| query | string | the jfql query *(no dbms commands!)* |

##### QueryRequest example

```json
{
  "token": "N99EXfcurUP+IZHB3rFb",
  "query": "select value * from users"
}
```

#### Close session

To close your session send a **POST** request to ``http://${server}:${port}/api/v1/session/close`` with a logout
request.

##### LogoutRequest structure

| Field |  Type  | Description       |
|:------|:------:|-------------------|
| token | string | the session token |

##### LogoutRequest example

```json
{
  "token": "N99EXfcurUP+IZHB3rFb"
}
```

#### Responses

##### GeneralResponse structure

| Field |     Type      | Description                                                         |
|:------|:-------------:|---------------------------------------------------------------------|
| state  | response state | the state of the response *(RESULT, FORBIDDEN, ERROR, SYNTAX_ERROR)* |

##### GeneralResponse example

```json
{
  "state": "FORBIDDEN"
}
```

##### ErrorResponse structure

| Field   |     Type      | Description              |
|:--------|:-------------:|--------------------------|
| state    | response state | the state of the response |
| message |    string     | the error message        |

##### ErrorResponse example

```json
{
  "state": "ERROR",
  "message": "Some error happened!"
}
```

##### SelectResponse structure

| Field     |     Type      | Description                                                |
|:----------|:-------------:|------------------------------------------------------------|
| state      | response state | the state of the response                                   |
| structure | string array  | the table structure of response                            |
| result    | column array  | the actual content *(a standalone string or column based)* |

##### SelectResponse example *(column based)*

```json
{
  "result": [
    {
      "content": {
        "password": "26f61a4f719705c0a0d075c8f995e456b295d10cb3b000275708cdd05abdd3f5",
        "name": "ByJoker",
        "id": "05348150",
        "email": "someone@byjoker.de"
      },
      "creation": 1637341356130
    }
  ],
  "state": "RESULT",
  "structure": [
    "id",
    "name",
    "password",
    "email"
  ]
}
```

##### SelectResponse example *(standalone)*

```json
{
  "result": [
    "N99EXfcurUP+IZHB3rFb"
  ],
  "state": "RESULT",
  "structure": [
    "Token"
  ]
}
```

### Configuration

***

#### GeneralConfig structure

| Field    |    Type     | Description                          |
|:---------|:-----------:|--------------------------------------|
| server   | json object | the settings for the internal server |
| security | json object | the security settings                |
| updater  | json object | the updater configuration            |

#### ServerConfig structure

| Field   |  Type   | Description                 |
|:--------|:-------:|-----------------------------|
| port    | integer | the port of internal server |
| enabled | boolean | the status of server        |

#### SecurityConfig structure

| Field                    |  Type   | Description                                                |
|:-------------------------|:-------:|------------------------------------------------------------|
| encryption               | string  | the encryption state of passwords                           |
| crossTokenRequests       | boolean | if requests at same session from different ips get blocked |
| memorySessions           | boolean | if sessions saved in memory not in file                    |
| onlyManualSessionControl | boolean | if client can create own sessions                          |
| jline                    | boolean | if [jline](https://github.com/jline/jline2) is enabled     |
| showQueries              | boolean | if incoming queries get displayed in console               |
| showConnections          | boolean | if incoming connections get displayed in console           |

#### UpdaterConfig structure

| Field      |  Type   | Description                                                                            |
|:-----------|:-------:|----------------------------------------------------------------------------------------|
| autoUpdate | boolean | if a new version gets directly downloaded                                              |
| host       | string  | the url to the updater json file [defaults](https://cdn.byjoker.de/myjfql/myjfql.json) |
| enabled    | boolean | if MyJFQL connector to update host                                                     |
| file       | string  | the file where the new version gets written in                                         |

#### Configuration example

```json
{
  "server": {
    "port": 2291,
    "enabled": true
  },
  "security": {
    "encryption": "BASE64",
    "crossTokenRequests": false,
    "memorySessions": false,
    "onlyManualSessionControl": true,
    "jline": true,
    "showQueries": false,
    "showConnections": false
  },
  "updater": {
    "autoUpdate": false,
    "file": "MyJFQL.jar",
    "host": "https://cdn.byjoker.de/myjfql/myjfql.json",
    "enabled": true
  }
}

```

### User

***

#### Create user

```myjfql
user create ${name} password ${password}
```

* If you want to create a database directly for the user with preset permissions you have to add ```database ${name}```
  and if you leave the name field empty it will be automatically the username.

#### Delete user

If you ever want to delete a user you can do it like this

```myjfql
user delete ${user}
```

#### User permission management

#### Grant accesses

```myjfql
user grant ${user} access ${access_type} at ${database} 
```

##### Revoke accesses

```myjfql
user revoke ${user} access from ${database} 
```

##### AccessTypes

* ``READ_WRITE``
* ``READ``
* ``NONE``

#### Display user

```myjfql
user display ${user}
```

#### List users

```myjfql
user list
```

### Version

***

#### Update version

```myjfql
version update ${version}
```

* To get the latest version leave the ``version`` field empty.

#### Display Version

```myjfql
version display
```

### Sessions

***

#### Open a session manually

```myjfql
sessions of ${user} open token ${token} database ${database} expire ${expire} address ${address} 
```

* The expiry format is ``yy-MM-dd hh:mm:ss`` and to expire never enter ``NEVER``.
* This only applies if you have **deactivated crossTokenRequests**: The address (s) specified is the one that a client
  must have in order for it to be able to connect to this session. You can specify one or more addresses, these must be
  separated with a ``,``. Entire IP address ranges can also be permitted by entering the prefix followed by a ``*``,
  see ``192.*``. All addresses that start with ``192`` can now connect if they have the token.

* You can leave every field empty to get it autofilled.

#### Displaying sessions

```myjfql
sessions of ${user}
```

#### Binding attributes

```myjfql
sessions of ${user} bind ${token} to ${section} ${value}
```

##### Sections

* ``DATABASE``
* ``ADDRESS``
* ``EXPIRE``

#### Close a session

```myjfql
sessions of ${user} close ${token}
```

#### Close all sessions

```myjfql
sessions of ${user} close-all
```

### Backup

***

#### Create backup

```myjfql
backup create ${name}
```

#### Delete backup

```myjfql
backup delete ${backup}
```

#### List backups

```myjfql
backup list
```

#### Display backup

```myjfql
backup display ${backup}
```

### Cli utils

***

#### Clear console screen

````myjfql
clear
````

#### Shutdown the dbms

````myjfql
shutdown
````

# JFQL

## Documentation

This documentation applies for version ``1.5.2``. Not everything mentioned works with older versions so be careful.

### Databases

***

#### Create a database *only as console*

````jfql
create database ${name} 
````

* **Only the console** can create databases!

#### Delete a database

````jfql
delete database ${database}
````

* **Only the console** can delete databases!

#### Switch between databases

````jfql
use database ${database}
````

#### List databases

```jfql
list databases
```

### Tables

***

#### Create a table

```jfql
create table ${name} structure ${structure} primary-key ${primary_key}
```

* The table will be created in the database you are currently working in.
* The structure are all the column names seperated by a space. Like this ``id username email password``.
* You can leave the ```primary-key``` field empty and the first structure item will be the primary key of that table.

#### Delete tables

````jfql
delete table ${table}
````

* You can only access tables of your using database.

#### List tables

```jfql
list tables from ${database}
```

* Leave the ``from`` field empty to see all tables.

#### Get structure of a table

```
structure of ${table}
```

* You can only access tables of your using database.
* To only see the primary-key add ``priamry-key`` to the query.

#### Mark other structure item as primary

```jfql
structure of ${table} mark-primary ${column_name}
```

#### Add entry to structure

```jfql
structure of ${table} add ${column_name}
```

#### Remove structure items

```jfql
structure of ${table} remove ${column_name}
```

#### Set a completely new table structure

```jfql
structure of ${table} set ${strucutre} primary-key ${primary_key}
```

* This is the same as if you set the structure of a **table while creating** it.

#### Insert into a table

You have three different possible ways of inserting data into your table:

1. Create a complete new dataset

    ```
    insert into ${table} key ${colum_names} value ${values}
    ```

    * This dataset **has to contain the primary key**!
    * The key field contains all colum names in which data is going to be inserted.
    * The value filed contains all values for the columns and every value belongs to the key at the same index. For
      example ``key id name value VALUE_OF_ID VALUE_OF_NAME``
    * Like everywhere an argument goes to the next space. If you want to prevent this add a ``'`` at start and end of
      your argument.

2. Edit one dataset with entering the primary key

    ```jfql
   insert into ${table} key ${colum_names} value ${values} primary-key ${primary_key}
   ```
    * The data fill be exactly inserted at in the first way but this data goes in the table entry with the entered
      primary key

3. Editing multiple dataset by passing conditions to them which get edited.

    ```jfql
    insert into ${table} key ${colum_names} value ${values} where ${conditions}
    ```

    * The data gets inserted like in the first and second example but only in the fields that pass the conditions.
    * The conditions are like simple if statements. For example ``name = 'WANTED_NAME''``. You can extend your
      conditions with ``and`` and ``or``
      and the creation of negative statement is as simple as this ``not name = 'WANDED_NAME'``
    * This conditions system applies also to the ``remove`` and ``select`` query.

#### Select values from table

Like in the insert query you have three possible ways to select data from a table:

1. Select every entry of the database

    ```jfql
   select value ${colum_names} from ${table}
   ```
    * You can also sort your response using the ``order`` tag. Enter a ``ASC`` or ``DESC`` as sequence. To sort a
      specific column add ``sort ${column_name}``.
    * To limit the amount of entries add ``limit ${limit}`` to your query.
    * The ``value`` field contains all colum names you want to select. Enter ``*`` to select all possible columns.

2. Select one table entry with entering the primary key
    ```jfql
   select value ${colum_names} from ${table} primary-key ${primary_key}
   ```
    * This is same as the first, but you only select one entry by the primary key.

3. Selecting multiple table entries by passing conditions to them which get edited.
    ```jfql
   select value ${colum_names} from ${table} where ${conditions}
   ```
    * **Look at the insert** query for the condition system.
    * The rest is like in the first example

#### Remove table entries

If you want to remove a column you can do this by passing the primary key or by passing conditions.

1. Using primary key
   ```jfql
   remove column ${primary_key_value} from ${table}
   ```

    * In case you want to remove all table entries enter ``*`` as primary key value.

2. By passing conditions
   ```jfql
   remove column * from ${table} where ${conditions}
   ```
    * **Look at the insert** query for the condition system.
    * All table entries which apply to these conditions get removed.

# License

All files on this repository are subject to the **MIT license**. Please read
the [LICENSE](https://github.com/ByJoker8625/MyJFQL/blob/master/LICENSE) file at the root of the project.
