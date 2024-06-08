Here is the guide of how to use the library :

# A - Create or load a Json #
## 1 - Create an empty Json ##
To create an empty json, you need to call the Json constructor with no argument :
````java
import fr.dtn.noobwork.io.data.Json;

public class Main {
    public static void main(String[] args) {
        Json json = new Json();
    }
}
````

## 2 - Load Json from a file ##
They are two methods to load a json file :
- Create a new Json instance using a String : the path to the file containing the json data
- Create a new Json instance using a File : the file containing the json data

Both methods are similar in code :
````java
import fr.dtn.noobwork.io.data.Json;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        Json jsonFromPath = new Json(System.getProperty("user.home") + "/Documents/demo.json");
        Json jsonFromFile = new Json(new File(System.getProperty("user.home"), "Documents/demo.json"));
    }
}
````

## 3 - Create a Json from a JSONObject instance ##
You can also create a Json instance using a JSONObject instance.

````java
import fr.dtn.noobwork.io.data.Json;
import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        object.put("key", "value");

        Json json = new Json(object);
    }
}
````

But it is not really useful, except internally in the library. Note that you can use the JSONObject class like a HashMap, because it extends of it.


# B - Set or replace data in the Json #
We can then add data into our empty json by using the method Json#set(String, Object) :
````java
import fr.dtn.noobwork.io.data.Json;

public class Main {
    public static void main(String[] args) {
        Json json = new Json();
        json.set("myKey", 42);
    }
}
````

Note that using Json#set(String, Object) create a new value for the given key or replace an old one if there is.

Only native types are written correctly :
- String
- byte/short/int/long
- float/double
- boolean
- arrays

All others type of objects are stored as String using Object#toString() method.

# C - Save the Json in a file #
After the instantiation of your Json object and the editing of the values in this json, you can save it in a file using one of the following methods :
- Json#save()
- Json#save(File)
- Json#save(String)

With the first one, you need to do one of those things :
- Instantiate using Json(String) or Json(File)
- Use Json#setFile(File) method

If you don't do it, the library will throw an exception because it cannot save the Json object without knowing the file to save in.

Using the second or the third method, you can specify a new file to save the Json in.

Here is an example :

````java
import fr.dtn.noobwork.io.data.Json;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Json json = new Json();

        json.set("students.grades", Arrays.asList(12, 18, 12));
        json.set("students.average", 14.0);
        json.set("students.names", new String[] { "Bob", "Bib", "Bub" });

        json.save(new File(System.getProperty("user.home"), "Documents/demo.json"));
    }
}
````
It gives us the following json file :
````json
{"students":{"average":14.0,"names":["Bob","Bib","Bub"],"grades":[12,18,12]}}
````
Note that the json file is written in a single line, it makes it really bad to read, so try using a Json beautifier to improve the reading.
