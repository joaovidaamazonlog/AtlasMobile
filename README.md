# Atlas Android

Aplicativo Android **100% nativo** para visualização geoespacial de dados do sistema Atlas. Sem backend próprio, consome dados via GitHub Pages com arquitetura clean, modular e totalmente offline-first.

## Características Principais

✨ **Arquitetura**
- Clean Architecture + MVVM
- Separação clara: Data → Domain → Presentation
- Totalmente testável e extensível
- Zero dependências de backend

🗺️ **Mapas**
- Renderização interativa com osmdroid
- Suporte a marcadores, clusters e GeoJSON
- Offline funcional com cache local

📊 **Dados**
- Consumo via Retrofit + Moshi (JSON)
- Cache inteligente com Room Database
- Sincronização automática on-demand
- TTL configurável por tipo de dado

🔌 **Resiliência**
- Funciona completamente offline
- Fallback automático para cache
- Tratamento robusto de erros
- Logging estruturado com Timber

---

## Stack Técnico

| Camada | Tecnologia |
|--------|-----------|
| **UI** | Jetpack Compose, Material Design 3 |
| **State** | StateFlow, ViewModel, Coroutines |
| **Injeção** | Hilt/Dagger |
| **HTTP** | Retrofit, OkHttp |
| **JSON** | Moshi (type-safe) |
| **Dados** | Room Database |
| **Mapas** | osmdroid (OpenStreetMap) |
| **Async** | Coroutines + Flow |
| **Logging** | Timber |

---

## Estrutura do Projeto

```
app/src/main/java/im/manus/atlas/
├── di/                 # Dependency Injection (Hilt)
├── data/              # Data Layer
│   ├── local/        # Room Database
│   ├── remote/       # Retrofit API
│   └── repository/   # Repository pattern
├── domain/           # Domain Layer (pure Kotlin)
│   ├── model/       # Business models
│   ├── repository/   # Repository interfaces
│   └── usecase/     # Use cases
└── presentation/     # Presentation Layer
    ├── ui/          # Compose Screens & Components
    └── viewmodel/   # ViewModels + State
```

---

## Como Usar

### Pré-requisitos
```bash
- Android Studio (Electric Eel ou superior)
- JDK 17+
- minSdk 24 (Android 7.0+)
```

### Build & Run

```bash
# Build debug APK
./gradlew build

# Executar em emulador/device
./gradlew installDebug

# Build release (otimizado)
./gradlew assembleRelease
```

### Não requer configuração adicional!

O app automaticamente consome dados de:
```
https://joaovidaamazonlog.github.io/atlas/data/dados_mapa.json
https://joaovidaamazonlog.github.io/atlas/data/optimization_data.geojson
```

---

## Fluxo de Dados

```
GitHub Pages (JSON)
       ↓
  Retrofit (HTTP)
       ↓
  AtlasApi Interface
       ↓
  Moshi (JSON → DTO)
       ↓
  Repository (Mapping)
       ↓
  Room Database (Cache)
       ↓
  UseCase (Business Logic)
       ↓
  ViewModel (State)
       ↓
  Compose UI (Render)
```

---

## Funcionalidades Implementadas

✅ Carregamento automático de parceiros
✅ Renderização de marcadores no mapa
✅ Cache inteligente (1 hora para parceiros)
✅ Fallback offline com dados cacheados
✅ Tratamento de erros com UI feedback
✅ Logging estruturado
✅ Dark mode support
✅ Componentes reutilizáveis

---

## Roadmap

### Curto Prazo (Próximas Sprints)
- [ ] Tela de detalhes de parceiro
- [ ] Filtros avançados (status, capacidade)
- [ ] Busca de parceiros
- [ ] Botão refresh manual
- [ ] Favoritos locais

### Médio Prazo
- [ ] Backend real (Supabase) para sync bidirecional
- [ ] Notificações push
- [ ] Rotas otimizadas
- [ ] Export de dados (PDF)
- [ ] Analytics

### Longo Prazo
- [ ] Multiplatform (iOS via Compose)
- [ ] Web (KMP)
- [ ] Realtime collaboration
- [ ] Integração Salesforce

---

## Limitações Conhecidas

| Limitação | Impacto | Solução |
|-----------|---------|---------|
| GeoJSON >10MB | Carregamento lento | Renderização seletiva (viewport) |
| Muitos markers (>1000) | Lag | Clustering automático |
| Sem edição remota | Read-only | Por design |
| Sem sincronização bidirecional | Atualizações manuais | Futuro: backend real |

---

## Decisões Arquiteturais

### Por que osmdroid em vez de Mapbox?
- ✅ Open source, sem custos
- ✅ Funciona 100% offline
- ✅ Suporte nativo a GeoJSON
- ❌ Menos features que Mapbox

### Por que Room em vez de Firestore?
- ✅ Total controle (RGPD)
- ✅ 100% offline
- ✅ Nenhuma dependência de backend
- ❌ Sem sincronização automática

### Por que Compose em vez de XML?
- ✅ Reativo (natural com StateFlow)
- ✅ Menos boilerplate
- ✅ Preparado para Multiplatform
- ❌ Menor ecosistema de libs

---

## Testabilidade

A arquitetura Clean permite testes unitários:

```kotlin
// Use case test (sem Android dependencies)
@Test
fun getPartners_success_returnsData() = runTest {
    val result = useCase()
    assertTrue(result.isSuccess)
}

// ViewModel test
@Test
fun loadData_setsLoadingThenSuccess() = runTest {
    assertEquals(MapUiState.Loading, viewModel.uiState.value)
    advanceUntilIdle()
    assertIs<MapUiState.Success>(viewModel.uiState.value)
}
```

---

## Performance & Otimizações

| Aspecto | Implementação |
|---------|--------------|
| **Memory** | Lazy loading, paginação de features |
| **Network** | OkHttp caching headers, Retrofit timeouts |
| **Battery** | Coroutines cancellation, refresh intervals |
| **Render** | Compose skipping, lazy column para listas |

---

## Segurança

✅ Todas as requisições via HTTPS
✅ OkHttp com interceptors configurados
✅ Nenhuma credencial no código
✅ RGPD-compliant (dados processados localmente)
✅ Validação de certificados SSL

---

## Debugging

### Logs
```
adb logcat | grep -i atlas
```

### Network Inspection
```
OkHttp interceptor automático (HttpLoggingInterceptor)
- Ativa em DEBUG builds
- Logging via Timber.d()
```

### Database Inspection
```
Android Studio → Device File Explorer →
data/data/im.manus.atlas/databases/atlas_database.db
```

---

## Contribuindo

1. Fork o repositório
2. Crie uma branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## Suporte

- 📖 Veja `ARCHITECTURE.md` para detalhes técnicos
- 🐛 Issues: [GitHub Issues](https://github.com/joaovidaamazonlog/atlas-android/issues)
- 📧 Contato: [seu email]

---

## Licença

MIT License - veja LICENSE.md para detalhes

---

**Desenvolvido com ❤️ usando Kotlin e Jetpack Compose**
