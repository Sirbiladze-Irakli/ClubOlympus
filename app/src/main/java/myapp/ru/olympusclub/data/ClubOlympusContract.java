package myapp.ru.olympusclub.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ClubOlympusContract {

    private ClubOlympusContract() {

    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "olympus";

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "myapp.ru.olympusclub";
    public static final String PATH_MEMBERS = "members";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);


    public static final class MemberEntry implements BaseColumns {

        public static final String TABLE_NAME = "members";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_FIRST_NAME = "firstName";
        public static final String COLUMN_LAST_NAME = "lastName";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_SPORT = "sport";

        public static final int UNKNOWN = 0;
        public static final int MALE = 1;
        public static final int FEMALE = 2;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MEMBERS);

        public static final String MULTIPLE_ITEMS =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MEMBERS;
        public static final String SINGLE_ITEMS =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_MEMBERS;

    }

}
