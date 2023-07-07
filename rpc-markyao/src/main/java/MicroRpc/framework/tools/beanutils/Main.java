package MicroRpc.framework.tools.beanutils;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John");
        map.put("age", 25);
        map.put("email", "john@example.com");

        Person person = MapToObjectConverter.convert(map, Person.class);

        System.out.println("Name: " + person.getName());
        System.out.println("Age: " + person.getAge());
        System.out.println("Email: " + person.getEmail());
    }
}

class Person {
    private String name;
    private int age;
    private String email;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
