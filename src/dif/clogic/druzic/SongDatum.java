package dif.clogic.druzic;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: choeseung-il
 * Date: 13. 9. 25.
 * Time: 오전 4:37
 * To change this template use File | Settings | File Templates.
 */
public final class SongDatum {

    public static final class CreateDB implements BaseColumns{

        public static final String NAME = "name";
        public static final String MELODYSEQUENCE = "melodysequence";
        public static final String _TABLENAME = "songdata";
        public static final String _CREATE =
                "create table " + _TABLENAME + " ("
                        + _ID + " integer primary key autoincrement, "
                        + NAME + " text not null , "
                        + MELODYSEQUENCE + " text not null );";
    }

}
