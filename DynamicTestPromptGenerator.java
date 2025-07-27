package com.yourplugin.sparklineageplugin.prompts;

public class DynamicTestPromptGenerator {
    
    // Dropdown parameter enums
    public enum Language { JAVA, SCALA }
    public enum Framework { NONE, SPARK, SPRING_BOOT, SPARK_WITH_SPRING }
    public enum TestFramework { JUNIT, TESTNG }
    
    // Configuration class to hold all dynamic parameters
    public static class TestConfig {
        private com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Language language;
        private String languageVersion;
        private com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework framework;
        private String sparkVersion;
        private String springBootVersion;
        private com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.TestFramework testFramework;
        private String mockitoVersion;
        private String testFrameworkVersion;
        private String packageName;
        private String testClassName;
        private String fullCode;

        private String dependentCode;
        
        // Constructor and getters/setters
        public TestConfig(com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Language language, String languageVersion, com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework framework,
                         String sparkVersion, String springBootVersion, com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.TestFramework testFramework,
                         String mockitoVersion, String testFrameworkVersion, String packageName,
                         String testClassName, String fullCode,String dependentCode) {
            this.language = language;
            this.languageVersion = languageVersion;
            this.framework = framework;
            this.sparkVersion = sparkVersion;
            this.springBootVersion = springBootVersion;
            this.testFramework = testFramework;
            this.mockitoVersion = mockitoVersion;
            this.testFrameworkVersion = testFrameworkVersion;
            this.packageName = packageName;
            this.testClassName = testClassName;
            this.fullCode = fullCode;
            this.dependentCode=dependentCode;
        }
        
        // Getters
        public com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Language getLanguage() { return language; }
        public String getLanguageVersion() { return languageVersion; }
        public com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework getFramework() { return framework; }
        public String getSparkVersion() { return sparkVersion; }
        public String getSpringBootVersion() { return springBootVersion; }
        public com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.TestFramework getTestFramework() { return testFramework; }
        public String getMockitoVersion() { return mockitoVersion; }
        public String getTestFrameworkVersion() { return testFrameworkVersion; }
        public String getPackageName() { return packageName; }
        public String getTestClassName() { return testClassName; }
        public String getFullCode() { return fullCode; }

        public String getDependentCode() {return dependentCode;}
    }
    
    /**
     * Main method to generate dynamic prompt based on configuration
     */
    public static String generatePrompt(TestConfig config) {
        StringBuilder prompt = new StringBuilder();
        
        // Base expert profile
        prompt.append(buildExpertProfile(config));
        
        // Core mission
        prompt.append(buildCoreMission(config));
        
        // Technical specifications
        prompt.append(buildTechnicalSpecs(config));
        
        // Framework-specific requirements
        prompt.append(buildFrameworkRequirements(config));
        
        // Language-specific requirements
        prompt.append(buildLanguageRequirements(config));
        
        // Testing framework requirements
        prompt.append(buildTestFrameworkRequirements(config));
        
        // Common requirements
        prompt.append(buildCommonRequirements(config));
        
        // Input code block
        prompt.append(buildInputSection(config));

        //DEPENDENCY CODE BLOCK
        prompt.append(buildDependencyCode(config));
        
        // Deliverable requirements
        prompt.append(buildDeliverableRequirements(config));
        
        // Final validation
        prompt.append(buildValidationChecklist(config));
        
        return prompt.toString();
    }
    
    private static String buildExpertProfile(TestConfig config) {
        return "You are a world-class senior software engineer with 30+ years of professional experience at top-tier technology companies including Google, Microsoft, Amazon, and Netflix. You are recognized as a domain expert in " +
                getLanguageExpertise(config.getLanguage()) + 
                getFrameworkExpertise(config.getFramework()) +
                " and advanced testing methodologies.\n\n";
    }
    
    private static String getLanguageExpertise(com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Language language) {
        switch (language) {
            case JAVA: return "Java enterprise development, JVM ecosystem";
            case SCALA: return "Scala functional programming, JVM ecosystem";
            default: return "JVM-based development";
        }
    }
    
    private static String getFrameworkExpertise(com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework framework) {
        switch (framework) {
            case SPARK: return ", Apache Spark distributed computing";
            case SPRING_BOOT: return ", Spring Boot microservices architecture";
            case SPARK_WITH_SPRING: return ", Apache Spark distributed computing, Spring Boot integration";
            default: return "";
        }
    }
    
    private static String buildCoreMission(TestConfig config) {
        return "=== CORE MISSION ===\n" +
                "I will provide you with a complete " + config.getLanguage().toString().toLowerCase() + 
                " code block containing a method chain starting from an entry point method, with all dependent methods concatenated from IntelliJ IDEA analysis. " +
                "Your mission is to generate comprehensive, production-grade unit tests that adhere to enterprise-level quality standards.\n\n";
    }
    
    private static String buildTechnicalSpecs(TestConfig config) {
        StringBuilder specs = new StringBuilder("=== TECHNICAL SPECIFICATIONS ===\n");
        
        specs.append("- ").append(config.getLanguage()).append(" Version: ").append(config.getLanguageVersion()).append("\n");
        
        if (config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPARK || config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPARK_WITH_SPRING) {
            specs.append("- Apache Spark Version: ").append(config.getSparkVersion()).append("\n");
        }
        
        if (config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPRING_BOOT || config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPARK_WITH_SPRING) {
            specs.append("- Spring Boot Version: ").append(config.getSpringBootVersion()).append("\n");
        }
        
        specs.append("- Mockito Version: ").append(config.getMockitoVersion()).append("\n");
        specs.append("- ").append(config.getTestFramework()).append(" Version: ").append(config.getTestFrameworkVersion()).append("\n");
        specs.append("- Test Framework: ").append(config.getTestFramework()).append(getFrameworkIntegration(config.getFramework())).append("\n\n");
        
        return specs.toString();
    }
    
    private static String getFrameworkIntegration(com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework framework) {
        switch (framework) {
            case SPARK: return " with SharedJavaSparkContext integration";
            case SPRING_BOOT: return " with Spring Boot Test integration";
            case SPARK_WITH_SPRING: return " with Spring Boot Test and SharedJavaSparkContext integration";
            default: return "";
        }
    }
    
    private static String buildFrameworkRequirements(TestConfig config) {
        StringBuilder requirements = new StringBuilder("=== FRAMEWORK-SPECIFIC REQUIREMENTS ===\n\n");
        
        switch (config.getFramework()) {
            case SPARK:
                requirements.append(buildSparkRequirements(config));
                break;
            case SPRING_BOOT:
                requirements.append(buildSpringBootRequirements(config));
                break;
            case SPARK_WITH_SPRING:
                requirements.append(buildSparkRequirements(config));
                requirements.append(buildSpringBootRequirements(config));
                break;
            case NONE:
                requirements.append(buildStandardJavaRequirements(config));
                break;
        }
        
        return requirements.toString();
    }
    
    private static String buildSparkRequirements(TestConfig config) {
        return "1. SPARK CONTEXT MANAGEMENT:\n" +
                "   - MUST create SparkSession in @Before/@BeforeEach method with proper configuration\n" +
                "   - Use SharedJavaSparkContext pattern for distributed testing\n" +
                "   - Configure SparkSession with appropriate settings: .master(\"local[*]\"), .appName(\"UnitTest\")\n" +
                "   - Include proper cleanup in @After/@AfterEach method to prevent resource leaks\n" +
                "   - Handle SparkContext lifecycle properly to avoid conflicts between tests\n\n" +
                
                "2. SPARK-SPECIFIC TESTING:\n" +
                "   - Test DataFrame/Dataset transformations using actual Spark operations\n" +
                "   - Verify schema correctness with StructType assertions\n" +
                "   - Test data transformations with sample input/output datasets\n" +
                "   - Include row count assertions: assertEquals(expectedCount, actualDataset.count())\n" +
                "   - Test column operations, filtering, groupBy, joins, and aggregations\n" +
                "   - Validate data types and null handling\n" +
                "   - Test custom UDFs (User Defined Functions) if present\n\n" +
                
                "3. SPARK IMPORTS - CONDITIONAL BASED ON CODE ANALYSIS:\n" +
                "   - Import org.apache.spark.sql.SparkSession ONLY if SparkSession is used\n" +
                "   - Import org.apache.spark.sql.Dataset/DataFrame ONLY if these types are present\n" +
                "   - Import org.apache.spark.sql.types.* ONLY if schema operations are detected\n" +
                "   - Import com.holdenkarau.spark.testing.SharedJavaSparkContext ONLY when using shared context\n" +
                "   - Import org.apache.spark.sql.functions.* ONLY if built-in functions (col, lit, etc.) are used\n" +
                "   - Import org.apache.spark.api.java.* ONLY if RDD operations are present\n\n";
    }
    
    private static String buildSpringBootRequirements(TestConfig config) {
        return "1. SPRING BOOT TESTING SETUP:\n" +
                "   - Use @SpringBootTest annotation ONLY for full integration tests\n" +
                "   - Use @WebMvcTest ONLY if web controllers are being tested\n" +
                "   - Use @DataJpaTest ONLY if repository/JPA entities are being tested\n" +
                "   - Use @MockBean ONLY when Spring beans need to be mocked\n" +
                "   - Configure test application properties ONLY if custom config is needed\n\n" +
                
                "2. SPRING BOOT SPECIFIC TESTING:\n" +
                "   - Test REST endpoints with MockMvc ONLY if REST controllers are present\n" +
                "   - Test service layer with proper dependency injection\n" +
                "   - Test repository layer with @DataJpaTest ONLY if repositories exist\n" +
                "   - Test configuration classes ONLY if custom configurations are detected\n" +
                "   - Test security configurations ONLY if security is implemented\n" +
                "   - Test actuator endpoints ONLY if actuator dependency is present\n\n" +
                
                "3. SPRING BOOT IMPORTS - CONDITIONAL BASED ON USAGE:\n" +
                "   - Import @SpringBootTest ONLY if full integration testing is required\n" +
                "   - Import @WebMvcTest ONLY if testing web layer (controllers)\n" +
                "   - Import @DataJpaTest ONLY if testing data layer (repositories)\n" +
                "   - Import MockMvc ONLY if web endpoint testing is needed\n" +
                "   - Import TestRestTemplate ONLY if integration testing with HTTP calls\n" +
                "   - Import @MockBean ONLY if Spring beans are being mocked\n" +
                "   - Import @Autowired ONLY if dependency injection is used in tests\n\n";
    }
    
    private static String buildStandardJavaRequirements(TestConfig config) {
        return "1. STANDARD JAVA TESTING:\n" +
                "   - Focus on unit testing individual methods and classes\n" +
                "   - Mock external dependencies using Mockito\n" +
                "   - Test business logic, edge cases, and error scenarios\n" +
                "   - Ensure proper exception handling\n" +
                "   - Test utility classes and helper methods\n\n";
    }
    
    private static String buildLanguageRequirements(TestConfig config) {
        StringBuilder requirements = new StringBuilder("=== LANGUAGE-SPECIFIC REQUIREMENTS ===\n\n");
        
        switch (config.getLanguage()) {
            case JAVA:
                requirements.append(buildJavaRequirements(config));
                break;
            case SCALA:
                requirements.append(buildScalaRequirements(config));
                break;
        }
        
        return requirements.toString();
    }
    
    private static String buildJavaRequirements(TestConfig config) {
        return "1. JAVA SYNTAX COMPLIANCE:\n" +
                "   - Strictly adhere to Java " + config.getLanguageVersion() + " syntax rules and language features\n" +
                "   - Use appropriate access modifiers (private, protected, public)\n" +
                "   - Static variables MUST NOT be directly referenced across classes - use proper accessor patterns\n" +
                "   - Follow Java naming conventions: camelCase for methods/variables, PascalCase for classes\n" +
                "   - Implement proper exception handling with try-catch blocks where appropriate\n" +
                "   - Use generics correctly and avoid raw types\n" +
                "   - Apply final keyword appropriately for immutable references\n\n" +
                
                "2. JAVA-SPECIFIC IMPORTS:\n" +
                "   - Standard Java imports: java.util.*, java.io.*, java.time.*, etc.\n" +
                "   - Java collections and streams API\n" +
                "   - Concurrency utilities if applicable\n\n";
    }
    
    private static String buildScalaRequirements(TestConfig config) {
        return "1. SCALA SYNTAX COMPLIANCE:\n" +
                "   - Strictly adhere to Scala " + config.getLanguageVersion() + " syntax rules and language features\n" +
                "   - Use appropriate access modifiers (private, protected, implicit)\n" +
                "   - Follow Scala naming conventions: camelCase for methods/variables, PascalCase for classes\n" +
                "   - Use Option/Some/None for null safety\n" +
                "   - Leverage functional programming constructs: map, filter, fold, etc.\n" +
                "   - Use case classes and pattern matching appropriately\n" +
                "   - Handle immutability and val/var distinctions correctly\n\n" +
                
                "2. SCALA-SPECIFIC IMPORTS:\n" +
                "   - Scala collections: scala.collection.mutable.*, scala.collection.immutable.*\n" +
                "   - Scala utilities: scala.util.{Try, Success, Failure}\n" +
                "   - Scala concurrent: scala.concurrent.Future, scala.concurrent.ExecutionContext\n\n";
    }
    
    private static String buildTestFrameworkRequirements(TestConfig config) {
        StringBuilder requirements = new StringBuilder("=== TEST FRAMEWORK REQUIREMENTS ===\n\n");
        
        switch (config.getTestFramework()) {
            case JUNIT:
                requirements.append(buildJUnitRequirements(config));
                break;
            case TESTNG:
                requirements.append(buildTestNGRequirements(config));
                break;
        }
        
        return requirements.toString();
    }
    
    private static String buildJUnitRequirements(TestConfig config) {
        return "1. JUNIT SETUP - CONDITIONAL IMPORTS:\n" +
                "   - Import @Test ONLY (always required for test methods)\n" +
                "   - Import @Before/@BeforeEach ONLY if setup methods are created\n" +
                "   - Import @After/@AfterEach ONLY if cleanup methods are created\n" +
                "   - Import @BeforeClass/@BeforeAll ONLY if class-level setup is needed\n" +
                "   - Import @RunWith ONLY if using JUnit 4 with runners\n" +
                "   - Import @ExtendWith ONLY if using JUnit 5 with extensions\n" +
                "   - Import @DisplayName ONLY if using JUnit 5 and descriptive names are added\n\n" +
                
                "2. JUNIT ASSERTIONS - IMPORT ONLY USED ASSERTIONS:\n" +
                "   - Import assertEquals ONLY if equality assertions are made\n" +
                "   - Import assertNotNull ONLY if null checks are performed\n" +
                "   - Import assertTrue/assertFalse ONLY if boolean assertions are used\n" +
                "   - Import assertThrows ONLY if exception testing is performed (JUnit 5)\n" +
                "   - Import assertTimeout ONLY if performance testing is needed\n" +
                "   - Use static imports ONLY for frequently used assertions\n\n" +
                
                "3. JUNIT VERSION-SPECIFIC IMPORTS:\n" +
                "   - JUnit 4: Import from org.junit.* packages\n" +
                "   - JUnit 5: Import from org.junit.jupiter.api.* packages\n" +
                "   - Import version-appropriate runner/extension classes only when needed\n\n";
    }
    
    private static String buildTestNGRequirements(TestConfig config) {
        return "1. TESTNG SETUP:\n" +
                "   - Use @Test annotation for all test methods\n" +
                "   - Use @BeforeMethod for setup methods\n" +
                "   - Use @AfterMethod for cleanup methods\n" +
                "   - Use @BeforeClass/@AfterClass for class-level setup\n" +
                "   - Use @DataProvider for parameterized tests\n" +
                "   - Configure test groups with groups attribute\n\n" +
                
                "2. TESTNG ASSERTIONS:\n" +
                "   - Use Assert.assertEquals, Assert.assertNotNull, Assert.assertTrue\n" +
                "   - Use expectedExceptions attribute for exception testing\n" +
                "   - Use timeOut attribute for performance testing\n" +
                "   - Import static org.testng.Assert.*\n\n" +
                
                "3. TESTNG IMPORTS:\n" +
                "   - org.testng.annotations.*\n" +
                "   - org.testng.Assert\n" +
                "   - org.testng.annotations.DataProvider\n\n";
    }
    
    private static String buildCommonRequirements(TestConfig config) {
        return "=== COMMON REQUIREMENTS ===\n\n" +
                
                "1. CLASS STRUCTURE & ANNOTATIONS:\n" +
                "   - Generate test class name as: " + config.getTestClassName() + "\n" +
                "   - Use package: " + config.getPackageName() + "\n" +
                "   - Include proper class-level annotations based on framework\n\n" +
                
                "2. IMPORT STATEMENTS - INTELLIGENT & CONDITIONAL:\n" +
                "   - Import ONLY what is actually used in the generated test code\n" +
                "   - Analyze the provided code to determine required imports\n" +
                "   - Testing imports: Include only the test framework annotations and assertions actually used\n" +
                "   - Mockito imports: Import only if mocking is actually performed (e.g., @Mock, when(), verify())\n" +
                "   - Framework-specific imports: Import only based on actual usage patterns detected\n" +
                "   - DO NOT include unused imports - keep imports clean and minimal\n" +
                "   - Standard library imports: Only include if specific classes/utilities are used\n\n" +
                
                "3. MOCKING STRATEGY - IMPORT ONLY WHEN NEEDED:\n" +
                "   - Use Mockito " + config.getMockitoVersion() + " for external dependencies ONLY\n" +
                "   - Import @Mock ONLY if fields are being mocked\n" +
                "   - Import @InjectMocks ONLY if dependency injection mocking is used\n" +
                "   - Import @Spy ONLY if partial mocking is required\n" +
                "   - Import when().thenReturn() ONLY if method stubbing is performed\n" +
                "   - Import ArgumentMatchers ONLY if flexible parameter matching is used\n" +
                "   - Import verify() ONLY if interaction verification is needed\n" +
                "   - DO NOT import Mockito classes if no mocking is required\n\n" +
                
                "4. TEST METHOD STRUCTURE:\n" +
                "   - Use descriptive test method names: testMethodName_WhenCondition_ThenExpectedResult\n" +
                "   - Follow AAA pattern: Arrange, Act, Assert\n" +
                "   - Include edge cases: null inputs, empty collections, boundary conditions\n" +
                "   - Test both positive and negative scenarios\n\n" +
                
                "5. ERROR HANDLING AND EDGE CASES:\n" +
                "   - Test exception scenarios with appropriate framework mechanisms\n" +
                "   - Validate error messages and exception types\n" +
                "   - Test timeout scenarios for long-running operations\n" +
                "   - Handle malformed data inputs gracefully\n\n";
    }
    
    private static String buildInputSection(TestConfig config) {
        return "=== INPUT CODE BLOCK ===\n" +
                "```" + config.getLanguage().toString().toLowerCase() + "\n" +
                config.getFullCode() + "\n" +
                "```\n\n";
    }

    private static String buildDependencyCode(TestConfig config)
    {
        if(config.getDependentCode()==null)
            return "";
        else
        return "If Above INPUT CODE BLOCK present, the below dependency block provides additional context for dependent classes/methods used within the above INPUT CODE BLOCK.\n" +
                "Do NOT generate test cases for below dependency classes — instead, use Mockito to mock them appropriately in the above code input block.\n"
                +"=== DEPENDENT CLASSES - FOR CONTEXT ONLY== \n"+
                "``` DEPENDENT CLASSES/METHODS \n"+
                config.getDependentCode()+"\n"
                +"``` \n "
                ;


    }
    
    private static String buildDeliverableRequirements(TestConfig config) {
        return "=== DELIVERABLE REQUIREMENTS ===\n\n" +
                "Return ONLY a complete, compilable unit test class that includes:\n" +
                " Complete package declaration and all necessary imports\n" +
                " Proper class declaration with required annotations\n" +
                " Setup and cleanup methods with proper resource management\n" +
                " Comprehensive test methods covering all identified scenarios\n" +
                " Proper mocking setup and verification\n" +
                " Exception handling and edge case coverage\n" +
                " Clear, professional code formatting and documentation\n\n" +
                "DON'T create any other new classes by assumptions \n"+
                "If there are other ClassType variables and used in method dont create those classes in Test , it means it is aleady created in code and just Mock those classes \n"+
                "IF you dont know the schema then dont craete data on your assumptions just mock it and also variables which are of other class also mock them and use proper @InjectMocks and @Mocks.\n"+
                "please please  note that only generate junit for ```java ``` code block and don't generate junit for ``` DEPENDENT CLASSES/METHODS , use ``` DEPENDENT CLASSES/METHODS ``` only for code context for java code block \n"+
                "=== OUTPUT FORMAT ===\n" +
                "```" + config.getLanguage().toString().toLowerCase() + "\n" +
                "// Generated unit test class code here\n" +
                "```\n\n";
    }
    
    private static String buildValidationChecklist(TestConfig config) {
        StringBuilder checklist = new StringBuilder("=== FINAL VALIDATION CHECKLIST ===\n" +
                "Before providing the response, ensure:\n" +
               // "- All imports are present and correct\n" +
                " " + config.getLanguage() + " " + config.getLanguageVersion() + " syntax compliance\n" +
                " " + config.getTestFramework() + " " + config.getTestFrameworkVersion() + " compatibility\n" +
                " Mockito " + config.getMockitoVersion() + " compatibility\n");
        
        if (config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPARK || config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPARK_WITH_SPRING) {
            checklist.append(" SparkSession is properly configured in setup method\n");
            checklist.append(" SharedJavaSparkContext integration\n");
        }
        
        if (config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPRING_BOOT || config.getFramework() == com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPARK_WITH_SPRING) {
            checklist.append(" Spring Boot Test annotations and configuration\n");
            checklist.append(" Proper Spring context management\n");
        }
        
        checklist.append(" No direct references to private static variables from other classes\n");
        checklist.append(" Comprehensive test coverage without assumed logic\n");
        checklist.append(" Proper resource cleanup and memory management\n");
        checklist.append(" Professional code quality and formatting\n\n");
        checklist.append(" Generate the complete unit test class now.");
        
        return checklist.toString();
    }
    
    // Example usage method
    public static void main(String[] args) {
        // Example configuration for Java + Spark + JUnit
        TestConfig config = new TestConfig(
                com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Language.JAVA,
            "11",
                com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.Framework.SPARK,
            "3.5.0",
            null, // Spring Boot version not needed for Spark-only
                com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState.TestFramework.JUNIT,
            "4.6.1",
            "5.8.2",
            "com.example.test",
            "MyServiceTest",
            "// Your full code here",""
        );
        
        String prompt = generatePrompt(config);
        System.out.println(prompt);
    }





}