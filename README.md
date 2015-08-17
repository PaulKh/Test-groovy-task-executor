# Test-groovy-task-executor

This is a test scheduler service for Groovy script tasks. 

Gradle tool was used to collect all the dependencies. 
Jetty library is used as a server framework with the usage of native java HttpServlet class.
For database support Sqlite has been chosen with pooling support.
Scheduler is made with the help of ThreadPoolExecutor. Tasks coming to the application are queued, 
maximum pool size is set to 4(only 4 tasks can be executed simultaneously, while other are waiting in the queue). 
In the case the queue is full new task will be rejected and marked as FAILED. 

Service provides REST API:

### Get all tasks from the server

GET request http://hostname:8080/tasks

Response:
```
[
   {
        "identifier": 33,
        "script": "class Person {\n    String name\n}\ndef p = new Person(name: 'Norman')\n sleep(10000) \np.getName()",
        "taskStatus": "SUCCEED",
        "result": "Norman"
    },
    {
        "identifier": 34,
        "script": "class Person {\n    String name\n}\ndef p = new Person(name: 'Norman')\n sleep(10000) \np.getName()",
        "taskStatus": "EXECUTING"
    }
    {
        "identifier": 35,
        "script": "class Person {\n    String name\n}\ndef p = new Person(name: 'Norman')\n sleep(10000) \np.getName()",
        "taskStatus": "FAILED"
    }
]
   ```
### Get the status and result of the task

GET request http://hostname:8080/tasks/result?id=5

`{"result":"Norman","status":"Succeed"}`

### Post new groovy script

POST request http://hostname:8080/tasks/add
```
{
    "script": "class Person {\n    String name\n}\ndef p = new Person(name: 'Norman')\n sleep(10000) \np.getName()"
}
```

### Delete task from the list, and stop the execution if still in progress

POST request http://hostname:8080/tasks/delete?id=1

`{"success":"OK"}`
