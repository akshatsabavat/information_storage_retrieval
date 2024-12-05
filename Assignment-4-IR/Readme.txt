I have added the previous HW2MainLucene.java file if needed can be run again to create the indexes like last time
Below is my run time results, it take 4 to 5 mins for the whole script to execute

904 Q0 XIE19970302.0144 1 0.0 MYRUN
904 Q0 XIE19970302.0144 2 0.0 MYRUN
904 Q0 XIE19961010.0123 3 0.0 MYRUN
904 Q0 XIE19961010.0123 4 0.0 MYRUN
904 Q0 XIE19961013.0085 5 0.0 MYRUN
904 Q0 XIE19961013.0085 6 0.0 MYRUN
904 Q0 XIE19961105.0192 7 0.0 MYRUN
904 Q0 XIE19961105.0192 8 0.0 MYRUN
904 Q0 XIE19971225.0050 9 0.0 MYRUN
904 Q0 XIE19961010.0139 10 0.0 MYRUN
904 Q0 XIE19961010.0139 11 0.0 MYRUN
904 Q0 XIE19971225.0050 12 0.0 MYRUN
904 Q0 XIE19960105.0012 13 0.0 MYRUN
904 Q0 XIE19960105.0012 14 0.0 MYRUN
904 Q0 XIE19961105.0131 15 0.0 MYRUN
904 Q0 XIE19961105.0131 16 0.0 MYRUN
904 Q0 XIE19961013.0080 17 0.0 MYRUN
904 Q0 XIE19961013.0080 18 0.0 MYRUN
904 Q0 XIE19961014.0209 19 0.0 MYRUN
904 Q0 XIE19961014.0209 20 0.0 MYRUN


4 queries search time: 4.346466666666666 min

Process finished with exit code 0

Some more instructions

I ran this on intelliJ

Look at the area just above the code editor where you see "Run" and "HW2MainLucene/HW4Main"
Click on the "HW2MainLucene" / "HW4main" dropdown, which is next to the top right play button
Select "Edit Configurations" from the menu that appears

After doing this, a new window will open where you can add the JVM arguments.

Copy and paste the following below in the JVM arguments feild

--add-opens java.base/jdk.internal.ref=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.nio=ALL-UNNAMED
--add-opens java.base/sun.nio.ch=ALL-UNNAMED