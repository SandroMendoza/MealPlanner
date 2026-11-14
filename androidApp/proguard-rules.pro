# Room (pulled in through WorkManager by the Google Mobile Ads SDK) creates each generated
# database class by reflection through its no-argument constructor. R8 full mode keeps a
# class's name without its constructors unless they are listed, so they are kept here.
-keep class * extends androidx.room.RoomDatabase {
    <init>();
}
