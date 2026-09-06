/*
 * ============================================================
 *
 *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *
 *  项目名称 : 智慧社区养老系统
 *  源码作者 : Tinsur (tinsur.cn)
 *  作者主页 : https://www.tinsur.cn
 *  联系方式 : me@tinsur.cn
 *  开源协议 : GPL 3.0
 *
 * ============================================================
 */
package cn.tinsur.elder;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;


public class MPGenerator {
    //需要配置
    private static final String dbTables = "check_in_record";             //需要生成的表名
    private static final Boolean enableSwagger = false;                 //是否开启Swagger

    public static void main(String[] args) {
        // 1.数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder("jdbc:mysql://localhost:3306/elder","root","1234").build();

        // 2.全局配置
        GlobalConfig.Builder globalConfigBuilder = new GlobalConfig.Builder();
        // 代码生成目录
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);// D:\MyIdeaProject\mybatis-plus
        globalConfigBuilder.outputDir(projectPath + "" + "/src/main/java");
        // 作者
        globalConfigBuilder.author("Tinsur");
        // 实体属性Swagger2注解
        if (enableSwagger){
            globalConfigBuilder.enableSwagger();
        }
        globalConfigBuilder.dateType(DateType.ONLY_DATE);//设置时间类型Date  LocalDateTime

        // 3.包配置
        PackageConfig.Builder packageConfigBuilder = new PackageConfig.Builder();
        packageConfigBuilder.parent("cn.tinsur.elder");           //项目包名

        //都有默认值   配置实体、mapper、service、controller的包名
        packageConfigBuilder.entity("pojo");

        // 4.策略配置
        StrategyConfig.Builder strategyConfigBuilder = new StrategyConfig.Builder();
        // 设置需要映射的表名  用逗号分割
        strategyConfigBuilder.addInclude(dbTables.split(","));
        strategyConfigBuilder.entityBuilder().idType(IdType.NONE);
        // 下划线转驼峰
        strategyConfigBuilder.entityBuilder().naming(NamingStrategy.underline_to_camel);
        strategyConfigBuilder.entityBuilder().columnNaming(NamingStrategy.underline_to_camel);
        // entity的Lombok
        strategyConfigBuilder.entityBuilder().enableLombok();
        // 逻辑删除
        strategyConfigBuilder.entityBuilder().logicDeleteColumnName("deleted");
        strategyConfigBuilder.entityBuilder().logicDeletePropertyName("deleted");

        // 自动填充
        // 创建时间
        IFill gmtCreate = new Column("create_time", FieldFill.INSERT);
        // 更新时间
        IFill gmtModified = new Column("update_time", FieldFill.INSERT_UPDATE);
        strategyConfigBuilder.entityBuilder().addTableFills(gmtCreate, gmtModified);
        // 乐观锁
        //strategyConfigBuilder.entityBuilder().versionColumnName("version");
        //strategyConfigBuilder.entityBuilder().versionPropertyName("version");
        // 使用RestController3
        strategyConfigBuilder.controllerBuilder().enableRestStyle();
        // 将请求地址转换为驼峰命名，如 http://localhost:8080/hello_id_2
        strategyConfigBuilder.controllerBuilder().enableHyphenStyle();

        // 创建代码生成器对象，加载配置   对应1.2.3.4步
        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig);
        autoGenerator.global(globalConfigBuilder.build());
        autoGenerator.packageInfo(packageConfigBuilder.build());
        autoGenerator.strategy(strategyConfigBuilder.build());

        // 执行
        autoGenerator.execute();
    }
}
