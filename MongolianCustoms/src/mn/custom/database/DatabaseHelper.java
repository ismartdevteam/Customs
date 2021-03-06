package mn.custom.database;


import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static String	databaseName	= "custom.dbo";
	private static int		databaseVersion	= 1;
	Dao<User, Integer>		userDao		= null;

	public DatabaseHelper(Context context) {
		super(context, databaseName, null, databaseVersion);
		// TODO Auto-generated constructor stub
	}

	// creating tables
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
			TableUtils.createTableIfNotExists(connectionSource, User.class);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// updgrade tables
	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

		try {
			TableUtils.createTable(connectionSource, User.class);
		
			onCreate(arg0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Dao<User, Integer> getUserDao() throws SQLException {
		if (userDao == null)
			userDao = getDao(User.class);
		return userDao;
	}


	// delete all videos from database
	public void deleteUser() {
		try {
			TableUtils.clearTable(connectionSource, User.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
		userDao = null;
		
		super.close();
	}

}
