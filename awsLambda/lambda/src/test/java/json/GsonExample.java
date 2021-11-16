package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GsonExample {

    public static void main(String[] args) {

        Staff staff = createDummyObject();

        //Gson gson = new Gson();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String json = gson.toJson(staff);
        System.out.println(json);
        
    }

    private static Staff createDummyObject() {

        Staff staff = new Staff();

        staff.setName("mkyong");
        staff.setAge(35);
        staff.setPosition("Founder");
        staff.setSalary(new BigDecimal("10000"));

        List<String> skills = new ArrayList<String>();
        skills.add("java");
        skills.add("python");
        skills.add("shell");

        staff.setSkills(skills);

        return staff;

    }

}

class Staff
{
	String name;
	int age;
	String position;
	BigDecimal salary;
	List<String> skills;
	
	
	public void setName(String n)
	{
		name = n;
	}
	
	public void setAge(int g)
	{
		age = g;
	}
	public void setPosition(String p)
	{
		position = p;
	}
	public void setSalary(BigDecimal s)
	{
		salary = s;
	}
	public void setSkills(List<String> s)
	{
		skills = s;
	}
}