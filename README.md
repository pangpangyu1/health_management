# 健康管理系统

## 1.技术栈

SSM + dubbo + SpringSecurity + Mysql + 七牛云 + Vue 

## 2.各模块功能

health_parent：父工程，打包方式为pom，统一锁定依赖的版本，同时聚合其他子模块便于统一执行maven命令

health_common：通用模块，打包方式为jar，存放项目中使用到的一些工具类、实体类、返回结果和常量类

health_interface：打包方式为jar，存放服务接口

health_service_provider：Dubbo服务模块，打包方式为war，存放服务实现类、Dao接口、Mapper映射文件等，作为服务提供方，需要部署到tomcat运行

health_backend：健康管理后台，打包方式为war，作为Dubbo服务消费方，存放Controller、HTML页面、js、css、spring配置文件等，需要部署到tomcat运行

health_mobile：移动端前台，打包方式为war，作为Dubbo服务消费方，存放Controller、HTML页面、js、css、spring配置文件等，需要部署到tomcat运行



角色：管理员和会员(预约用户)

### 管理员：

- 会员管理
- 检查项管理
- 检查组管理
- 套餐管理
- 预约设置
- 预约管理
- 预约情况查看

### 会员

- 注册预约

## 3.开发中遇到的问题和细节

### 表关系

- checkGroup-checkItem 一对多
- setmeal-checkGroup 一对多

一对多实现，例如进行套餐编辑时，通过setmealId与checkGroup的关系映射找到setmeal的子检查组，再通过关系映射找到自检查项

```xml
<resultMap type="com.pangpangyu.pojo.CheckGroup" id="findByIdResultMap" extends="baseResultMap">
    <collection property="checkItems" javaType="ArrayList" ofType="com.pangpangyu.pojo.CheckItem"
                column="id" select="com.pangpangyu.dao.CheckItemDao.findCheckItemById">
    </collection>
</resultMap>
```

```xml
<resultMap type="com.pangpangyu.pojo.Setmeal" id="findByIdResultMap" extends="baseResultMap">
    <collection property="checkGroups" javaType="ArrayList" ofType="com.pangpangyu.pojo.CheckGroup"
                column="id" select="com.pangpangyu.dao.CheckGroupDao.findCheckGroupById">
    </collection>
</resultMap>
```

### 使用SpringSecurity进行后台权限控制

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:security="http://www.springframework.org/schema/security"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://www.springframework.org/schema/mvc
                        http://www.springframework.org/schema/mvc/spring-mvc.xsd
                        http://code.alibabatech.com/schema/dubbo
                        http://code.alibabatech.com/schema/dubbo/dubbo.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context.xsd
                          http://www.springframework.org/schema/security
                          http://www.springframework.org/schema/security/spring-security.xsd">

    <!--
        http：用于定义相关权限控制
        指定哪些资源不需要进行权限校验，可以使用通配符
    -->
    <security:http security="none" pattern="/js/**" />
    <security:http security="none" pattern="/css/**" />
    <security:http security="none" pattern="/img/**" />
    <security:http security="none" pattern="/plugins/**" />
    <security:http security="none" pattern="/login.html" />

    <!--
        http：用于定义相关权限控制
        auto-config：是否自动配置
                        设置为true时框架会提供默认的一些配置，例如提供默认的登录页面、登出处理等
                        设置为false时需要显示提供登录表单配置，否则会报错
        use-expressions：用于指定intercept-url中的access属性是否使用表达式
    -->
    <security:http auto-config="true" use-expressions="true">
        <security:headers>
            <!--设置在页面可以通过iframe访问受保护的页面，默认为不允许访问-->
            <security:frame-options policy="SAMEORIGIN"></security:frame-options>
        </security:headers>
        <!--
            intercept-url：定义一个拦截规则
            pattern：对哪些url进行权限控制
            access：在请求对应的URL时需要什么权限，默认配置时它应该是一个以逗号分隔的角色列表，
                  请求的用户只需拥有其中的一个角色就能成功访问对应的URL
            isAuthenticated()：已经经过认证（不是匿名用户）
        -->
        <security:intercept-url pattern="/pages/**"  access="isAuthenticated()" />
        <!--form-login：定义表单登录信息-->
        <security:form-login login-page="/login.html"
                             username-parameter="username"
                             password-parameter="password"
                             login-processing-url="/login.do"
                             default-target-url="/pages/main.html"
                             always-use-default-target="true"
                             authentication-failure-url="/login.html"
        />
        <!--
            csrf：对应CsrfFilter过滤器
            disabled：是否启用CsrfFilter过滤器，如果使用自定义登录页面需要关闭此项，
                    否则登录操作会被禁用（403）
        -->
        <security:csrf disabled="true"></security:csrf>

        <security:logout logout-url="/logout.do"
                         logout-success-url="/login.html" invalidate-session="true"/>
    </security:http>

    <!--配置密码加密对象-->
    <bean id="passwordEncoder"
          class="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder" />

    <!--认证管理器，用于处理认证操作-->
    <security:authentication-manager>
        <!--认证提供者，执行具体的认证逻辑-->
        <security:authentication-provider user-service-ref="springSecurityUserService">
            <!--指定密码加密策略-->
            <security:password-encoder ref="passwordEncoder" />
        </security:authentication-provider>
    </security:authentication-manager>

    <!--开启注解方式权限控制-->
    <security:global-method-security pre-post-annotations="enabled" />
</beans>
```



### 使用MD5对预约用户密码进行加密

```java
//保存会员信息
public void add(Member member) {
    String password = member.getPassword();
    if(password != null){
        //使用md5将明文密码进行加密
        password = MD5Utils.md5(password);
        member.setPassword(password);
    }
    memberDao.add(member);
}
```



### 使用POI技术实现xls表格的导入与导出

```java
package com.pangpangyu.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class POIUtils {
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";
    private final static String DATE_FORMAT = "yyyy/MM/dd";
    /**
     * 读入excel文件，解析后返回
     * @param file
     * @throws IOException
     */
    public static List<String[]> readExcel(MultipartFile file) throws IOException {
        //检查文件
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行,因为一般第一行为标题
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
            workbook.close();
        }
        return list;
    }

    //校验文件是否合法
    public static void checkFile(MultipartFile file) throws IOException{
        //判断文件是否存在
        if(null == file){
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if(!fileName.endsWith(xls) && !fileName.endsWith(xlsx)){
            throw new IOException(fileName + "不是excel文件");
        }
    }
    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith(xls)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith(xlsx)){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }
    public static String getCellValue(Cell cell){
        String cellValue = "";
        if(cell == null){
            return cellValue;
        }
        //如果当前单元格内容为日期类型，需要特殊处理
        String dataFormatString = cell.getCellStyle().getDataFormatString();
        if(dataFormatString.equals("m/d/yy")){
            cellValue = new SimpleDateFormat(DATE_FORMAT).format(cell.getDateCellValue());
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
}
```

### 使用七牛云和Redis存储图片

使用七牛云存储图片，redis做缓存使用，例如在新建检查项时，可能会多次选择图片，将每次选择的图片名称放入redis的一个临时集合中，最后将操作成功的图片名称保存到最终集合中

```java
public class RedisConstant {
    //套餐图片所有图片名称
    public static final String SETMEAL_PIC_RESOURCES = "setmealPicResources";
    //套餐图片保存在数据库中的图片名称
    public static final String SETMEAL_PIC_DB_RESOURCES = "setmealPicDbResources";
}
```

```java
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        //设置检查组和检查项的多对多关联关系，操作t_setmeal_checkgroup表
        this.setSetmealAndCheckGroup(setmealId,checkgroupIds);
        //将图片名称保存到Redis集合中
        savePic2Redis(setmeal.getImg());
        //比对两个集合差异，将垃圾图片删除
//        deleteTrashPic();

        //新增套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }


    private void savePic2Redis(String pic){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
    }

    private void remPic2Redis(String pic){
        jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
    }
```

### 使用quartz定时任务组件和定时清理垃圾图片

通过redis中临时集合和最终集合的对比，将不存在于最终集合的图片名称通过七牛云工具类在服务器上进行删除，同时将redis临时集合中的对应键删除

```java
public void clearImg(){
    //根据Redis中保存的两个set集合进行差值计算，获得垃圾图片名称集合
    Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
    if(set != null){
        for (String picName : set) {
            //删除七牛云服务器上的图片
            QiniuUtils.deleteFileFromQiniu(picName);
            //从Redis集合中删除图片名称
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);
            System.out.println("定时清理执行！");
        }
    }
}
```

quartz配置

```xml
    <!-- 注册一个触发器，指定任务触发的时间 -->
    <bean id="myTrigger" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
        <!-- 注入JobDetail -->
        <property name="jobDetail" ref="jobDetail"/>
        <!-- 指定触发的时间，基于Cron表达式 -->
        <property name="cronExpression">
<!--            <value>0 0 2 * * ?</value>-->
<!--            每隔十秒清除一次-->
                <value>0/10 * * * * ?</value>
        </property>
    </bean>
    <!-- 注册一个统一的调度工厂，通过这个调度工厂调度任务 -->
    <bean id="scheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
        <!-- 注入多个触发器 -->
        <property name="triggers">
            <list>
                <ref bean="myTrigger"/>
            </list>
        </property>
    </bean>
```

利用FreeMarker生成移动端静态页面

```java
   @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        //设置检查组和检查项的多对多关联关系，操作t_setmeal_checkgroup表
        this.setSetmealAndCheckGroup(setmealId,checkgroupIds);
        //将图片名称保存到Redis集合中
        savePic2Redis(setmeal.getImg());
        //比对两个集合差异，将垃圾图片删除
//        deleteTrashPic();

        //新增套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }

    //生成静态页面
    public void generateMobileStaticHtml() {
        //准备模板文件中所需的数据
        List<Setmeal> setmealList = setmealDao.findAll();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情静态页面（多个）
        generateMobileSetmealDetailHtml(setmealList);
    }

    //生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("setmealList", setmealList);
        this.generateHtml("mobile_setmeal.ftl","m_setmeal.html",dataMap);
    }

    //生成套餐详情静态页面（多个）
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal : setmealList) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("setmeal", setmealDao.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl",
                    "setmeal_detail_"+setmeal.getId()+".html",
                    dataMap);
        }
    }

    private void generateHtml(String templateName,String htmlPageName,Map<String, Object> dataMap) {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Writer out = null;
            try {
                // 加载模版文件
                Template template = configuration.getTemplate(templateName);
                // 生成数据
                File docFile = new File(outputpath + "\\" + htmlPageName);
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
                // 输出文件
                template.process(dataMap, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != out) {
                        out.flush();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
    }
```

利用zookeeper作为dubbo的服务注册中心来实现服务注册

```xml
<!--指定暴露服务的端口，如果不指定默认为20880-->
<dubbo:protocol name="dubbo" port="20887"/>
<!--指定服务注册中心地址-->
<dubbo:registry address="zookeeper://127.0.0.1:2181"/>
<!--批量扫描，发布服务-->
<dubbo:annotation package="com.pangpangyu.service"/>
```

## 4.总结

本次项目对所学大部分技术都有使用，熟悉了基于SSM的分模块开发流程，也简单利用了zookeeper和dubbo构建分布式系统，学到了分页 查询，日历操作，报表和图文等操作。