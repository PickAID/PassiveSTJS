package com.pickaid.passivestjs.kubejs.probe;

import zzzank.probejs.api.dump.CustomDump;
import zzzank.probejs.docs.assignments.SpecialTypes;
import zzzank.probejs.lang.snippet.SnippetDump;
import zzzank.probejs.lang.transpiler.Transpiler;
import zzzank.probejs.lang.typescript.RequestAwareFiles;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Set;

public final class PassiveSTJSLegacyProbePlugin implements ProbeJSPlugin {
    static String specialTypesGlobalName() {
        return "special_types";
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        PassiveSTJSLegacyProbeTypes.assignWrappedTypes(scriptDump);
    }

    @Override
    public void denyTypes(Transpiler transpiler) {
        PassiveSTJSLegacyProbeTypes.deniedWrapperClasses().forEach(transpiler::reject);
    }

    @Override
    public void modifyFiles(RequestAwareFiles files) {
        PassiveSTJSLegacyProbeTypes.rewriteWrapperFiles(files);
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        scriptDump.addChild(new CustomDump(
                scriptDump.writeTo().resolve(".passivestjs_legacy_cleanup"),
                path -> PassiveSTJSLegacyProbeTypes.cleanupLegacyGlobalFiles(scriptDump.writeTo())
        ));
        Wrapped.Namespace special = new Wrapped.Namespace(SpecialTypes.NAMESPACE);
        PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().forEach(special::addCode);
        scriptDump.addGlobal(specialTypesGlobalName(), special);
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        return PassiveSTJSLegacyProbeJava.providedClasses();
    }

    @Override
    public void addVSCodeSnippets(SnippetDump dump) {
        PassiveSTJSLegacyProbeSnippets.addSnippets(dump);
    }
}
