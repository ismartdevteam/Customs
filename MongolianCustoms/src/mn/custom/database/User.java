package mn.custom.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class User {
@DatabaseField
public String id;
@DatabaseField
public String fname;
@DatabaseField
public String lname;
@DatabaseField
public String register;
@DatabaseField
public String lang;
@DatabaseField
public String CSTMCD;
@DatabaseField
public String mobile;
@DatabaseField
public String tel;
@DatabaseField
public String email;
@DatabaseField
public String address;
@DatabaseField
public String pageCount;
}
