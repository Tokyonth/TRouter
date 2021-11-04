package com.tokyonth.trouter.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tokyonth.trouter.annotation.AssemblyType;
import com.tokyonth.trouter.annotation.TRouterAnt;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class TRouterProcessor extends AbstractProcessor {

    Messager messager;
    Elements elements;
    Filer filer;
    Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(TRouterAnt.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(TRouterAnt.class);
        for (Element element : elements) {
            TRouterAnt alias = element.getAnnotation(TRouterAnt.class);
            try {
                generateCode(element, alias, (TypeElement) element);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void generateCode(Element e, TRouterAnt alias, TypeElement typeElement) throws IOException {
        print("TRouter link--->" + alias.path() + ":" + e.toString());
        TypeMirror typeActivity = elements.getTypeElement(AssemblyType.ACTIVITY.getClassName()).asType();
        TypeMirror asType = typeElement.asType();
        AssemblyType assemblyType;
        if (types.isSubtype(asType, typeActivity)) {
            print("TRouter type--->" + typeActivity);
            assemblyType = AssemblyType.ACTIVITY;
        } else {
            print("TRouter type--->" + asType);
            assemblyType = AssemblyType.FRAGMENT;
        }

        ClassName assemblyClass = ClassName.get(Constants.ANNOTATION_PACKAGE_NAME, "AssemblyType");
        ClassName metaType = ClassName.get(Constants.ANNOTATION_PACKAGE_NAME, "TRouteMeta");
        ClassName stringType = ClassName.get("java.lang", "String");
        ClassName mapClass = ClassName.get("java.util", "Map");
        TypeName resultMap = ParameterizedTypeName.get(mapClass, stringType, metaType);

        MethodSpec.Builder holderBuilder = MethodSpec.methodBuilder("onLoader")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(resultMap, "holders")
                .addStatement("holders.put($S, new $T($T." + assemblyType + ", $S, $S))",
                        alias.path(),
                        metaType,
                        assemblyClass,
                        alias.path(),
                        e.toString());
        MethodSpec holderMethodSpec = holderBuilder.build();

        ClassName superMethod = ClassName.get(Constants.CORE_PACKAGE_NAME, Constants.INTERFACE_NAME);
        TypeSpec className = TypeSpec.classBuilder(alias.path().replace("/", "C$"))
                .addJavadoc(Constants.DOC_CONTENT)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(superMethod)
                .addMethod(holderMethodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(Constants.BUILD_CLASS_PATH, className)
                .build();

        javaFile.writeTo(filer);
    }

    private void print(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

}
