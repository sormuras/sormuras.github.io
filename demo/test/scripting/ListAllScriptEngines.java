package scripting;

import javax.script.ScriptEngineManager;

class ListAllScriptEngines {
  public static void main(String[] args) {
    var manager = new ScriptEngineManager();
    var factories = manager.getEngineFactories();
    for (var factory : factories) {
      System.out.println("===");
      System.out.println("factory.getEngineName()      -> " + factory.getEngineName());
      System.out.println("factory.getEngineVersion()   -> " + factory.getEngineVersion());
      System.out.println("factory.getLanguageName()    -> " + factory.getLanguageName());
      System.out.println("factory.getLanguageVersion() -> " + factory.getLanguageVersion());
      System.out.println("factory.getExtensions()      -> " + factory.getExtensions());
      System.out.println("factory.getMimeTypes()       -> " + factory.getMimeTypes());
      System.out.println("factory.getNames()           -> " + factory.getNames());
    }
  }
}
