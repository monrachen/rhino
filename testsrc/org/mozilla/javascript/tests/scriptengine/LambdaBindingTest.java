package org.mozilla.javascript.tests.scriptengine;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.engine.RhinoScriptEngineFactory;

public class LambdaBindingTest {

    private static ScriptEngineManager manager;

    private ScriptEngine engine;

    @BeforeClass
    public static void init() {
        manager = new ScriptEngineManager();
        manager.registerEngineName("rhino", new RhinoScriptEngineFactory());
    }

    @Before
    public void setup() {
        engine = manager.getEngineByName("rhino");
    }
    
    @Test
    public void test() throws ScriptException, NoSuchMethodException {
        // these operations is valid in Nashorn.
        final Console console = new Console();
        engine.put("console", console);
        engine.put("func1", (Runnable) () -> {
            console.log("func1");
        });
        engine.put("func2", (Consumer<String>) (str) -> {
            console.log("func2:" + str);
        });
        engine.put("func3", (Supplier<String>) () -> {
            return "func3";
        });
        engine.put("func4", (Function<String, String>) (str) -> {
            return "func4:" + str;
        });
        engine.put("func5", (Function4P<String, Number, Number, Boolean, String>) (str1, num2, num3, bool4) -> {
            return "func5:" + str1 + ":" + num2 + ":" + num3 + ":" + bool4;
        });
        String script = "" +
           "func1();" +
           "func2(\"hello2\");" +
           "console.log(func3());" +
           "console.log(func4(\"hello4\"));" +
           "console.log(func5(\"hello5\", 127, 3.14, false));" +
           "var funcRef = func5;" +
           "console.log(funcRef(\"hello5R\", 127, 3.14, false));" +
           "var funcWrap = function() { return func5(\"hello5W\", 127, 3.14, false); };" +
           "console.log(funcWrap());" + 
           "";
        engine.eval(script);
    }
    
    @FunctionalInterface
    public static interface Function4P<P1, P2, P3, P4, R> {
        
        public R apply(P1 p1, P2 p2, P3 p3, P4 p4);
        
    }
    
    public static class Console {
        public void log(Object obj) {
//            System.out.println(obj);
        }
    }

}
