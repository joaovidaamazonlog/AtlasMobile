# Atlas Android - Documentação de Arquitetura

## 1. Visão Geral

O **Atlas Android** é um aplicativo nativo 100% Kotlin que consome dados estáticos do GitHub Pages (Sistema Atlas). É uma aplicação **offline-first** que sincroniza dados quando a conexão está disponível.

### Princípios Arquiteturais

- ✅ **Não há backend próprio** - Todos os dados vêm do GitHub Pages
- ✅ **Clean Architecture** + MVVM - Separação clara de responsabilidades
- ✅ **Offline-first** - Funciona com cache local via Room
- ✅ **Reativo** - Baseado em Coroutines e Flow
- ✅ **Testável** - Dependências injetáveis, regras de negócio independentes de framework

---

## 2. Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                          │
│  ├─ UI Screens (Compose)                               │
│  ├─ ViewModels (StateFlow)                             │
│  └─ Components (Reutilizáveis)                         │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│               DOMAIN LAYER                               │
│  ├─ Models (Partner, DeliveryStation)                  │
│  ├─ Repositories (Interfaces)                          │
│  └─ Use Cases (GetPartners, FilterByStatus)            │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                DATA LAYER                                │
│  ├─ Remote (Retrofit + AtlasApi)                       │
│  ├─ Local (Room Database)                              │
│  ├─ Mappers (DTO ↔ Entity ↔ Domain)                    │
│  └─ Repository Implementation                           │
└─────────────────────────────────────────────────────────┘
```

### 2.1 Presentation Layer

**Responsabilidade**: Gerenciar UI e estado visível ao usuário

**Componentes**:
- `MapScreen.kt` - Tela principal com mapa interativo
- `MapViewModel.kt` - Gerencia estado (loading, success, error)
- Componentes reutilizáveis (`LoadingIndicator`, `ErrorView`, `EmptyState`)

**Estado**:
```kotlin
sealed class MapUiState {
    object Loading : MapUiState()
    data class Success(val partners: List<Partner>) : MapUiState()
    data class Error(val message: String) : MapUiState()
}
```

### 2.2 Domain Layer

**Responsabilidade**: Lógica de negócio pura (sem dependências Android)

**Componentes**:
- **Models**: `Partner`, `DeliveryStation`, `GeoJsonFeature`
- **Repository Interfaces**: Define contrato de acesso a dados
- **Use Cases**: Operações de negócio
  - `GetPartnersUseCase` - Recuperar todos os parceiros
  - `GetDeliveryStationsUseCase` - Recuperar estações de entrega
  - `FilterPartnersByStatusUseCase` - Filtrar parceiros por status

### 2.3 Data Layer

**Responsabilidade**: Acesso e persistência de dados

#### Remote Data Source (Retrofit)
```kotlin
interface AtlasApi {
    @GET("dados_mapa.json")
    suspend fun getPartnersData(): PartnerDataResponse

    @GET("optimization_data.geojson")
    @Streaming
    suspend fun getOptimizationGeoJson(): ResponseBody
}
```

#### Local Data Source (Room)
```
┌─ PartnerEntity (Table: partners)
├─ DeliveryStationEntity (Table: delivery_stations)
└─ DAOs para acesso
```

#### Repository Pattern
```kotlin
interface AtlasRepository {
    suspend fun getPartners(): Result<List<Partner>>
    suspend fun getDeliveryStations(): Result<List<DeliveryStation>>
    suspend fun filterPartnersByStatus(status: String): Result<List<Partner>>
    suspend fun getGeoJsonFeatures(): Flow<GeoJsonFeature>
}
```

---

## 3. Fluxo de Dados

### Carregamento Inicial (Sincronização)

```
1. Usuário abre app
   ↓
2. MapViewModel init{} chama loadData()
   ↓
3. GetPartnersUseCase é executado
   ↓
4. AtlasRepository tenta atualizar dados remotos
   ├─ Busca dados em GitHub Pages (JSON)
   ├─ Converte DTO para Entity
   ├─ Persiste em Room Database
   └─ Converte Entity para Domain Model
   ↓
5. StateFlow atualiza UI com Partners
   ↓
6. MapScreen renderiza marcadores no mapa
```

### Acesso Offline

Se a rede não está disponível:
```
1. Repository captura exceção de rede
   ↓
2. Tenta recuperar dados em cache (Room)
   ├─ Se cache existe → Success
   └─ Se cache vazio → Error
   ↓
3. UI exibe dados cacheados ou mensagem de erro
```

---

## 4. Stack Tecnológica

| Tecnologia | Versão | Propósito |
|------------|--------|----------|
| **Kotlin** | 1.9.0 | Linguagem |
| **Jetpack Compose** | 2023.08 | UI Declarativa |
| **Hilt** | 2.48 | Dependency Injection |
| **Retrofit** | 2.9.0 | Cliente HTTP |
| **Moshi** | 1.15.0 | JSON Parsing |
| **OkHttp** | 4.11.0 | HTTP Client |
| **Room** | 2.6.1 | Persistência Local |
| **Coroutines** | 1.7.3 | Async/Non-blocking |
| **osmdroid** | 6.1.18 | Mapas OpenStreetMap |
| **Timber** | 5.0.1 | Logging |

---

## 5. Cache e Offline

### Estratégia de Cache

```kotlin
// Dados de parceiros: TTL 1 hora
PARTNERS_TTL = Duration.ofHours(1)

// Estações de entrega: TTL indefinido (dados estáticos)
STATIONS_TTL = Duration.ofDays(365)

// GeoJSON: TTL 24 horas
GEOJSON_TTL = Duration.ofHours(24)
```

### Implementação

```
Room Database (atlas_database.db)
├─ partners table
│  └─ cached_at: timestamp para invalidação
├─ delivery_stations table
│  └─ cached_at: timestamp para invalidação
└─ Queries com Dao pattern
```

### Comportamento Offline

| Ação | Offline | Online |
|------|---------|--------|
| Ver mapa | ✅ (cache) | ✅ (sync) |
| Filtrar parceiros | ✅ | ✅ |
| Ver detalhes | ✅ (cache) | ✅ |
| Sincronizar dados | ❌ | ✅ |

---

## 6. Estrutura de Arquivos

```
app/src/main/java/im/manus/atlas/
├── di/
│   └── AppModule.kt (Hilt config, Retrofit, Room, Repository)
│
├── data/
│   ├── local/
│   │   ├── AtlasDatabase.kt
│   │   ├── entity/ (PartnerEntity, DeliveryStationEntity)
│   │   └── dao/ (PartnerDao, DeliveryStationDao)
│   │
│   ├── remote/
│   │   └── AtlasApi.kt + DTOs
│   │
│   ├── repository/
│   │   └── AtlasRepositoryImpl.kt
│   │
│   └── mapper/
│       ├── PartnerMapper.kt
│       └── DeliveryStationMapper.kt
│
├── domain/
│   ├── model/ (Partner, DeliveryStation, GeoJsonFeature)
│   ├── repository/ (AtlasRepository interface)
│   └── usecase/ (GetPartners, FilterByStatus, etc)
│
├── presentation/
│   ├── ui/
│   │   ├── MapScreen.kt
│   │   ├── components/ (LoadingIndicator, ErrorView, EmptyState)
│   │   └── theme/ (Colors, Theme, Typography)
│   │
│   └── viewmodel/
│       └── MapViewModel.kt
│
├── util/
│   ├── Constants.kt
│   ├── Extensions.kt
│   ├── ResultExt.kt
│   └── Logging utils
│
└── AtlasApplication.kt (Hilt entry point)
```

---

## 7. Decisões Técnicas

### 7.1 Por que Jetpack Compose?

✅ **Prós**:
- UI declarativa e reativa (integração natural com StateFlow)
- Menos boilerplate que ViewBinding
- Recomposições otimizadas automaticamente
- Preparado para Compose Multiplatform (futuro)

❌ **Contras**:
- Curva de aprendizado maior
- Performance pode variar em devices antigos

### 7.2 Por que osmdroid em vez de Mapbox?

✅ **osmdroid**:
- Open source, sem custos
- Suporte nativo a GeoJSON
- Já configurado no projeto

❌ **Limitações**:
- Menos features que Mapbox
- Documentação menos completa
- Performance em arquivos GeoJSON muito grandes pode ser desafiadora

**Solução para GeoJSON >10MB**:
- Renderização seletiva (viewport-based)
- Limitar features visíveis a 500
- Simplificação geométrica

### 7.3 Por que Room em vez de Firebase/Supabase?

✅ **Room**:
- Sem dependência de backend
- Total controle sobre dados
- RGPD-compliant (dados locais)
- Funciona 100% offline

❌ **Cons**:
- Sem sincronização automática
- Sem compartilhamento entre dispositivos

### 7.4 Por que Hilt em vez de Koin?

✅ **Hilt**:
- Oficial do Android
- Compile-time safety
- Melhor suporte a ViewModel
- Integração com Jetpack libraries

---

## 8. Limitações Conhecidas

### Performance

| Limitação | Impacto | Mitigação |
|-----------|---------|-----------|
| GeoJSON >10MB | Carregamento lento | Streaming parsing, renderização seletiva |
| Muitos markers (>1000) | Lag ao scroll | Clustering, virtualização |
| Atualização frequente | Battery drain | Cache com TTL sensato |

### Funcionalidade

- ❌ Edição de dados remotamente (read-only)
- ❌ Sincronização bidirecional
- ❌ Offline-first completo (GeoJSON requer internet)
- ❌ Multilingue (PT-BR only atualmente)

---

## 9. Como Rodar Localmente

### Pré-requisitos

```bash
- Android Studio (Electric Eel+)
- JDK 17+
- Gradle 8.2+
- minSdk: 24
- targetSdk: 34
```

### Build e Run

```bash
# Clone o repositório
git clone https://github.com/joaovidaamazonlog/atlas-android

# Build
./gradlew build

# Run em emulator/device
./gradlew installDebug

# Run testes
./gradlew test
```

### Configuração de Desenvolvimento

O aplicativo consome dados automaticamente de:
```
https://joaovidaamazonlog.github.io/atlas/data/dados_mapa.json
https://joaovidaamazonlog.github.io/atlas/data/optimization_data.geojson
```

Nenhuma configuração adicional necessária!

---

## 10. Fluxo de Sincronização de Dados

### Primeira Execução (Cold Start)

```
1. App abre → MapViewModel.init()
2. loadData() → GetPartnersUseCase()
3. Repository.getPartners()
   ├─ Tenta buscar de network
   ├─ Parse JSON com Moshi
   └─ Persiste em Room
4. UI mostra dados
```

### Atualizações Subsequentes

```
1. Usuário puxa para refresh (futuro)
2. Repository.getPartners() chamado
3. Mesmo fluxo acima
4. Dados antigos substituídos
```

### Comportamento em Falha de Rede

```
1. Network request falha
2. Repository captura exceção
3. Tenta servir cache local
   ├─ Se disponível → Success
   └─ Se vazio → Error
4. UI exibe estado apropriado
```

---

## 11. Segurança

### Data Protection

- ✅ Dados cacheados localmente (Room não é criptografado por padrão)
- ✅ HTTPS para todas as requisições
- ✅ Nenhuma credencial armazenada
- ✅ RGPD-compliant (dados processados localmente)

### Network Security

- ✅ OkHttp com interceptors para logging
- ✅ Timeouts configurados (padrão 30s)
- ✅ Validação de certificados HTTPS
- ✅ Nenhuma API key exposta no código

### RLS e Permissões

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## 12. Evolução Futura

### Curto Prazo (Sprint Próximo)

- [ ] Tela de detalhes de parceiro
- [ ] Filtros avançados (status, capacidade, jurisdição)
- [ ] Dark mode automático
- [ ] Busca/search de parceiros
- [ ] Favoritos locais

### Médio Prazo

- [ ] Backend real (Supabase) para sincronização
- [ ] Notificações push
- [ ] Rotas otimizadas (routing engine)
- [ ] Export de dados (PDF, CSV)
- [ ] Analytics e crash reporting

### Longo Prazo

- [ ] iOS via Compose Multiplatform
- [ ] Web version (KMP)
- [ ] Realtime collaboration
- [ ] Machine learning para prospecting difficulty
- [ ] Integração com Salesforce

---

## 13. Testes

### Estrutura de Testes (A Implementar)

```
src/test/
├── domain/
│   ├── usecase/ (Unit tests)
│   └── model/ (Entity tests)
│
├── data/
│   ├── repository/ (Mock tests)
│   └── mapper/ (Transformation tests)
│
└── presentation/
    └── viewmodel/ (ViewModel tests)
```

### Exemplo de Teste

```kotlin
@Test
fun getPartners_success_returnsData() = runTest {
    // Given
    val mockData = listOf(
        Partner(storeId = "1", name = "Test", ...)
    )
    coEvery { repository.getPartners() } returns Result.success(mockData)

    // When
    val result = useCase()

    // Then
    assertTrue(result.isSuccess)
    assertEquals(mockData, result.getOrNull())
}
```

---

## 14. Troubleshooting

### "Erro ao carregar dados"

**Causa**: Sem conexão de internet
**Solução**: Verifique internet, tente refresh

### Mapa não aparece

**Causa**: osmdroid não inicializado
**Solução**: Verifique Configuration.getInstance() em MapScreen

### Database bloqueado

**Causa**: Múltiplas coroutines acessando simultaneamente
**Solução**: Room é thread-safe, mas evite transactions longas

### Performance baixa com muitos markers

**Causa**: Renderização de 1000+ features
**Solução**: Use clustering ou paginação

---

## 15. Referências Úteis

- [Google Clean Architecture Guide](https://developer.android.com/topic/architecture)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [osmdroid Project](https://osmdroid.github.io/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)

---

**Versão**: 1.0
**Última Atualização**: 2026-02-14
**Autor**: Arquiteto de Software Mobile Sênior
