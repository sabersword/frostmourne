### 如果启动查看效果

1. 3个项目jar包 + asm-*.jar + TestControll.class放到同一目录
2. java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6666 -jar demo-1.0-SNAPSHOT.jar
3. java -Xbootclasspath/a:/usr/lib/jvm/java/lib/tools.jar -jar attach-1.0-SNAPSHOT.jar PID weave org.ypq.demo.controller.TestController test
4. java -Xbootclasspath/a:/usr/lib/jvm/java/lib/tools.jar -jar attach-1.0-SNAPSHOT.jar PID reset org.ypq.demo.controller.TestController test
5. java -Xbootclasspath/a:/usr/lib/jvm/java/lib/tools.jar -jar attach-1.0-SNAPSHOT.jar PID redefine org.ypq.demo.controller.TestController test
6. 访问localhost:8080/test?age=1&name=saber&companyName=abc

