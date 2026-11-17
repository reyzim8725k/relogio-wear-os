# Relógio Wear OS

Relógio digital simples para Wear OS, em Java, sem dependências externas.

## Compilar (debug)

Requisitos: JDK 17, Android SDK (platform 34, build-tools 34), Gradle 8.7+.

```bash
gradle wrapper --gradle-version 8.7   # gera o gradlew, se necessário
./gradlew :app:assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

## Estrutura

- `app/src/main/java/com/exemplo/relogio/MainActivity.java`: lógica do relógio (atualiza a cada segundo).
- `app/src/main/res/layout/activity_main.xml`: layout com hora, segundos e data.
- `app/src/main/AndroidManifest.xml`: manifesto com suporte a Wear OS.
