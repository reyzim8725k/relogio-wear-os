# Chronos Relógio Premium para Wear OS

Aplicativo Wear OS reconstruído do zero em Kotlin e Jetpack Compose for Wear OS. O design AMOLED usa preto profundo, acento champanhe/dourado, navegação por lista com suporte à coroa/rotary input, feedback háptico e modo ambient de baixo brilho.

## Catálogo

O menu contém 49 telas independentes: oito faces de relógio; cronômetro, temporizadores, pomodoro, alarmes, respiração e meditação; calendário e feriados do Brasil; calculadoras, conversores, dados, senha, lanterna, bússola e nível; passos, água, humor, batimentos e hidratação; notas, tarefas, metrônomo, cozinha, bateria, dispositivo e configurações.

## Build

- Kotlin 1.9.24 / Android Gradle Plugin 8.5.2
- Compose for Wear OS 1.4.0
- JDK 17, Android SDK 34, minSdk 30
- `./gradlew :app:assembleDebug`
- `./gradlew :app:assembleRelease`

Os dados simples de uso são persistidos em SharedPreferences. Sensores têm fallback seguro; permissões de corpo e atividade estão declaradas no manifesto para solicitação pelo sistema quando aplicável.
