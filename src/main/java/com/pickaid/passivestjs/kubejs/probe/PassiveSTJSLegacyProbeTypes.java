package com.pickaid.passivestjs.kubejs.probe;

import zzzank.probejs.docs.assignments.SpecialTypes;
import zzzank.probejs.lang.typescript.RequestAwareFiles;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.java.clazz.ClassPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class PassiveSTJSLegacyProbeTypes {
    private static final List<String> LEGACY_GLOBAL_FILES = List.of(
            "passivestjs_special_types.d.ts",
            "passivestjs_id_aliases.d.ts"
    );

    private PassiveSTJSLegacyProbeTypes() {
    }

    static void assignWrappedTypes(ScriptDump scriptDump) {
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            wrapperTypeDeclarations(alias).forEach(type -> scriptDump.assignType(alias.wrapperClass(), type.name, type.type));
        }
    }

    static Collection<TypeDecl> specialTypeDeclarations() {
        List<TypeDecl> declarations = new ArrayList<>();
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            declarations.add(new TypeDecl(alias.alias(), specialType(alias)));
        }
        return declarations;
    }

    static Collection<TypeDecl> wrapperTypeDeclarations() {
        List<TypeDecl> declarations = new ArrayList<>();
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            declarations.addAll(wrapperTypeDeclarations(alias));
        }
        return declarations;
    }

    static void rewriteWrapperFiles(RequestAwareFiles files) {
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            TypeScriptFile file = files.requestOrCreate(ClassPath.fromJava(alias.wrapperClass()));
            rewriteWrapperCodes(file.codes, alias.wrapperClass().getSimpleName(), alias.alias());
        }
    }

    static Set<Class<?>> deniedWrapperClasses() {
        LinkedHashSet<Class<?>> denied = new LinkedHashSet<>();
        for (PassiveSTJSLegacyProbeIdAliases.IdAlias alias : PassiveSTJSLegacyProbeIdAliases.all()) {
            denied.add(alias.wrapperClass());
        }
        return Set.copyOf(denied);
    }

    static void cleanupLegacyGlobalFiles(Path scriptRoot) {
        Path globalDir = scriptRoot.resolve("global");
        for (String fileName : LEGACY_GLOBAL_FILES) {
            Path path = globalDir.resolve(fileName);
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to delete legacy PassiveSTJS ProbeJS file " + path, exception);
            }
        }
    }

    static void rewriteWrapperCodes(List<Code> codes, String wrapperSimpleName, String specialAliasName) {
        BaseType specialType = Types.primitive(SpecialTypes.dot(specialAliasName));
        String wrapperName = "$" + wrapperSimpleName;
        String wrapperTypeName = wrapperName + "$$Type";
        String globalAliasName = wrapperName + "_";

        codes.removeIf(code -> code instanceof TypeDecl typeDecl
                && (wrapperName.equals(typeDecl.name) || wrapperTypeName.equals(typeDecl.name)));
        codes.removeIf(code -> code instanceof Wrapped.Global);

        TypeDecl wrapperAlias = new TypeDecl(wrapperName, specialType);
        TypeDecl wrapperTypeAlias = new TypeDecl(wrapperTypeName, specialType);
        Wrapped.Global global = new Wrapped.Global();
        global.addCode(new TypeDecl(globalAliasName, Types.primitive(wrapperTypeName)));

        codes.add(0, global);
        codes.add(0, wrapperTypeAlias);
        codes.add(0, wrapperAlias);
    }

    private static Collection<TypeDecl> wrapperTypeDeclarations(PassiveSTJSLegacyProbeIdAliases.IdAlias alias) {
        BaseType type = Types.primitive(SpecialTypes.dot(alias.alias()));
        String wrapperName = "$" + alias.wrapperClass().getSimpleName();
        return List.of(
                new TypeDecl(wrapperName, type),
                new TypeDecl(wrapperName + "$$Type", type)
        );
    }

    private static BaseType specialType(PassiveSTJSLegacyProbeIdAliases.IdAlias alias) {
        if (alias.specialTypeReference() != null && !alias.specialTypeReference().isBlank()) {
            return Types.primitive(alias.specialTypeReference());
        }
        return idUnion(alias.ids().get());
    }

    private static BaseType idUnion(Collection<String> ids) {
        List<BaseType> literals = ids.stream()
                .sorted()
                .distinct()
                .map(Types::literal)
                .map(BaseType.class::cast)
                .toList();
        if (literals.isEmpty()) {
            return Types.STRING;
        }
        if (literals.size() == 1) {
            return literals.get(0);
        }
        return Types.or(literals);
    }
}
