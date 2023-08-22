### Java实现modbus-tcp通信
#### 依赖引入
引入modbus4j依赖
```xml
    <dependency>
    <groupId>com.infiniteautomation</groupId>
    <artifactId>modbus4j</artifactId>
    <version>3.0.5</version>
    </dependency>
    <!--modbus4j所在仓库-->

    <repositories>
    <repository>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <id>ias-snapshots</id>
        <name>Infinite Automation Snapshot Repository</name>
        <url>https://maven.mangoautomation.net/repository/ias-snapshot/</url>
    </repository>
    <repository>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>ias-releases</id>
        <name>Infinite Automation Release Repository</name>
        <url>https://maven.mangoautomation.net/repository/ias-release/</url>
    </repository>
</repositories>
```


