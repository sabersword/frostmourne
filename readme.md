### 如果启动查看效果

1. 3个项目jar包+javassist.jar放到同一目录
2. java -jar demo-1.0-SNAPSHOT.jar
3. java -Xbootclasspath/a:/usr/lib/jvm/java/lib/tools.jar -jar attach-1.0-SNAPSHOT.jar PID weave
4. java -Xbootclasspath/a:/usr/lib/jvm/java/lib/tools.jar -jar attach-1.0-SNAPSHOT.jar PID reset
5. java -Xbootclasspath/a:/usr/lib/jvm/java/lib/tools.jar -jar attach-1.0-SNAPSHOT.jar PID redefine
6. 访问localhost:8080/test?age=999&name=saber

