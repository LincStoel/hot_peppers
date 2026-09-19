# Hot Peppers — implementation progress

Full plan: `C:\Users\linco\.claude\plans\i-want-to-implement-atomic-stonebraker.md`

## Status as of last session

All 8 implementation steps are done. `./gradlew build` succeeds with no warnings, and
`./gradlew runServer` reaches `Done (...)! For help, type "help"` with no exceptions and no
resource-loading warnings (recipes, loot table, tags, damage type, data map, and loot modifier
JSON all parse and resolve cleanly).

### Done
- **Step 1 (Gradle bootstrap):** `build.gradle` / `settings.gradle` / `gradle.properties` /
  wrapper (Gradle 9.2.1) — unchanged from last session, verified working.
- **Step 2 (art import):** all 6 textures + generated `spicy.png` mob-effect icon — unchanged
  from last session.
- **Step 3 (registries):** `HotPeppers.java` — `DeferredRegister`s for blocks, items, mob
  effects, data components, recipe serializers, and (needed for Step 7) global loot modifier
  serializers, all wired in the `@Mod` constructor. `SpicyEffect.java` is the small `MobEffect`
  subclass. API surface (`FoodProperties.Builder`, `DataComponentType.Builder`,
  `DeferredRegister.DataComponents`, etc.) was verified against the decompiled NeoForge/Minecraft
  jars in the Gradle cache (`~/.gradle/caches/ng_execute/.../outputs.jar` has the actual
  recompiled-with-Parchment classes; `javap -p`/`-c` against those was the source of truth,
  since `brewing-1.21.1` — the style reference from the original plan — is no longer present on
  disk).
- **Step 4 (`HotPepperCropBlock`):** 4-stage crop modeled on `BeetrootBlock`. **Caught and fixed
  a real bug via `runServer`:** `CropBlock.createBlockStateDefinition()` adds its own
  hardcoded `AGE` static field (bound to `AGE_7` at class-init), not the virtual
  `getAgeProperty()` — so overriding `getAgeProperty()` alone silently left the block state
  definition without an `AGE_3` property at all, and block construction threw
  `IllegalArgumentException: Cannot set property ... as it does not exist`. Fixed by also
  overriding `createBlockStateDefinition` to add `getAgeProperty()`, matching what
  `BeetrootBlock` itself does.
- **Step 5 (`Spice.java` + `SpiceHandler.java`):** stacking/ignite on eat, faster natural regen
  on tick, mirroring vanilla `FoodData.tick()` guards.
- **Step 6 (`SpicyFoodRecipe.java` + `SpicyTooltip.java`):** dynamic `CustomRecipe` for
  pepper+food -> spicier food with renamed item; client tooltip line.
- **Step 7 (`AddItemModifier.java` + jungle grass seed drop):** generic add-item
  `LootModifier`, gated entirely by loot conditions in JSON (biome, block type, chance) rather
  than in Java.
- **Step 8 (resources):** blockstates, block/item models, lang, loot table, damage type, item
  tag, both recipes, both loot-modifier JSON files, and the compostables data map — all present
  and verified loading with no warnings.

### Bugs found and fixed via `runServer` (not just `./gradlew build`)
1. `createBlockStateDefinition` override missing on `HotPepperCropBlock` (see Step 4 above) —
   a **compile-time-invisible** bug; `./gradlew build` alone would not have caught this.
2. NeoForge 21.1's `ComposterBlock.COMPOSTABLES` static map is **deprecated**; the replacement is
   a JSON *data map* (`neoforge:compostables`, type `net.neoforged.neoforge.registries.datamaps.builtin.Compostable`).
   Switched to `data/neoforge/data_maps/item/compostables.json` (no Java code needed) — removed
   `HotPeppers.commonSetup`/`FMLCommonSetupEvent` entirely.
3. **Data map files must live under the data map type's own namespace**, not the contributing
   mod's namespace — i.e. `data/neoforge/data_maps/item/compostables.json`, not
   `data/hot_peppers/data_maps/...`. This surprised me: it's unlike tags (which merge contributions
   from every namespace at each mod's own path); data maps use the exact same file path as the
   type's own namespace, and multiple mods contributing to the same builtin data map all place
   their JSON at that identical path (`ResourceManager` stacks resources at identical paths across
   packs, similar to how multiple datapacks resolve one `data/minecraft/tags/...` file). Confirmed
   via `net.neoforged.neoforge.registries.DataMapLoader.load()` (`FileToIdConverter` derives the
   attachment id straight from the file's own namespace+path).

## Decisions locked in (from plan Q&A, do not re-ask)
- Toolchain: NeoForge + ModDevGradle, flat package `com.lincstoel.hotpeppers`.
- Tool check for no-burn harvest: **hoes and shears only** (`hot_peppers:safe_harvest_tools` item tag).
- Bare-hand harvest damage: **1.0 (half heart)**, no added effect, custom `spicy_burn` damage type.
- Spicy food naming: tooltip line **and** renamed item. Prefixes: "Spicy " / "Very Spicy " /
  **"Extremely Spicy "** (rank 3 — user corrected from "Blazing" to "Extremely").
- Spicy caps at level III; a dose that would exceed III instead ignites the player 3s and does not
  raise the level further.
- Regen bonus must speed up *natural* regen only (no Regeneration effect added), still costs
  exhaustion like vanilla natural healing.

## Session housekeeping (survives new chats since it's a file, not context)
- `~/.claude/settings.json`: `permissions.blockReadsOutsideWorkingDirectories` was set to `false`,
  and `permissions.additionalDirectories` includes `E:\Modding\Minecraft\magneticism` and the
  OneDrive art folder. `brewing-1.21.1` is gone from `E:\Modding\Minecraft` (confirmed absent
  again this session) — no longer usable as a reference; `magneticism` was used instead for
  registration/event-bus style.

## Not yet done (manual/interactive — see the plan's Verification section)
Everything in the plan's numbered Verification list (1-7) requires actually playing the game
(planting, timing growth, bare-hand harvest damage, spice stacking/ignite, regen timing,
crafting, jungle seed drops) — not something this session could exercise headlessly.
`./gradlew runClient` was not attempted (no confirmed display in this environment); `runServer`
was used instead purely to catch registration/data-loading errors, which it did (twice — see
above). Recommend the user run `./gradlew runClient` themselves and work through the
Verification section next.
