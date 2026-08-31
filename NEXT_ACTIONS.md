# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 24/24 (100.0%)
- **Function parity:** 229/538 matched (target 552) — 42.6%
- **Class/type parity:** 60/158 matched (target 149) — 38.0%
- **Combined symbol parity:** 289/696 matched (target 701) — 41.5%
- **Average inline-code cosine:** 0.08 (function body across 20 matched files)
- **Average documentation cosine:** 0.15 (doc text across 20 matched files)
- **Cheat-zeroed Files:** 18
- **Critical Issues:** 23 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. mapping

- **Target:** `serdeyaml.Mapping [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 3
- **Priority Score:** 3437510.0
- **Functions:** 26/56 matched (target 71)
- **Missing functions:** `keys`, `values`, `values_mut`, `equivalent`, `hash`, `is_key_into`, `index_into`, `index_into_mut`, `swap_remove_from`, `swap_remove_entry_from`, `shift_remove_from`, `shift_remove_entry_from`, `partial_cmp`, `total_cmp`, `iter_cmp_by`, `index`, `index_mut`, `extend`, `from_iter`, `into_iter`, `or_insert_with`, `and_modify`, `into_mut`, `into_key`, `serialize`, `deserialize`, `expecting`, `visit_unit`, `visit_map`, `fmt`
- **Types:** 6/19 matched (target 8)
- **Missing types:** `Index`, `Output`, `Iter`, `Item`, `IntoIter`, `IterMut`, `Keys`, `IntoKeys`, `Values`, `ValuesMut`, `IntoValues`, `Visitor`, `Value`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/mapping.rs` vs expected `mapping.rs`
- **Proposed provenance header:** `// port-lint: source mapping.rs` (current: `// port-lint: source serde_yaml/src/mapping.rs`)
- **Lint issues:** 1

### 2. libyaml.tag

- **Target:** `libyaml.Tag [PROVENANCE-FALLBACK]`
- **Similarity:** 0.51
- **Dependents:** 2
- **Priority Score:** 2010605.0
- **Functions:** 4/4 matched (target 12)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/libyaml/tag.rs` vs expected `libyaml/tag.rs`
- **Proposed provenance header:** `// port-lint: source libyaml/tag.rs` (current: `// port-lint: source serde_yaml/src/libyaml/tag.rs`)
- **Lint issues:** 1

### 3. error

- **Target:** `serdeyaml.Error [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2002210.0
- **Functions:** 16/16 matched (target 23)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 24)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/error.rs` vs expected `error.rs`
- **Proposed provenance header:** `// port-lint: source error.rs` (current: `// port-lint: source serde_yaml/src/error.rs`)
- **Lint issues:** 1

### 4. libyaml.cstr

- **Target:** `libyaml.Cstr [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2000810.0
- **Functions:** 7/7 matched (target 14)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/libyaml/cstr.rs` vs expected `libyaml/cstr.rs`
- **Proposed provenance header:** `// port-lint: source libyaml/cstr.rs` (current: `// port-lint: source serde_yaml/src/libyaml/cstr.rs`)
- **Lint issues:** 1

### 5. path

- **Target:** `serdeyaml.Path [PROVENANCE-FALLBACK]`
- **Similarity:** 0.12
- **Dependents:** 2
- **Priority Score:** 2000308.8
- **Functions:** 1/1 matched (target 6)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 7)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/path.rs` vs expected `path.rs`
- **Proposed provenance header:** `// port-lint: source path.rs` (current: `// port-lint: source serde_yaml/src/path.rs`)
- **Lint issues:** 1

### 6. de

- **Target:** `serdeyaml.De [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1859110.0
- **Functions:** 4/72 matched (target 21)
- **Missing functions:** `de`, `deserialize_any`, `deserialize_bool`, `deserialize_i8`, `deserialize_i16`, `deserialize_i32`, `deserialize_i64`, `deserialize_i128`, `deserialize_u8`, `deserialize_u16`, `deserialize_u32`, `deserialize_u64`, `deserialize_u128`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_enum`, `deserialize_identifier`, `deserialize_ignored_any`, `peek_event`, `peek_event_mark`, `next_event`, `next_event_mark`, `jump`, `ignore_any`, `visit_sequence`, `visit_mapping`, `end_sequence`, `fmt`, `end_mapping`, `recursion_check`, `next_element_seed`, `next_key_seed`, `next_value_seed`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `visit_scalar`, `parse_borrowed_str`, `parse_null`, `parse_bool`, `parse_unsigned_int`, `parse_signed_int`, `parse_negative_int`, `parse_f64`, `digits_but_not_number`, `visit_int`, `visit_untagged_scalar`, `is_plain_or_tagged_literal_scalar`, `invalid_type`, `expecting`, `parse_tag`, `enum_tag`
- **Types:** 2/19 matched (target 3)
- **Missing types:** `Result`, `Item`, `Error`, `Event`, `DeserializerFromEvents`, `CurrentEnum`, `Nest`, `ExpectedSeq`, `ExpectedMap`, `SeqAccess`, `MapAccess`, `EnumAccess`, `Variant`, `UnitVariantAccess`, `Void`, `InvalidType`, `Value`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/de.rs` vs expected `de.rs`
- **Proposed provenance header:** `// port-lint: source de.rs` (current: `// port-lint: source serde_yaml/src/de.rs`)
- **Lint issues:** 1

### 7. with

- **Target:** `serdeyaml.With [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1242710.0
- **Functions:** 2/106 matched (target 8)
- **Missing functions:** `serialize_bool`, `serialize_i8`, `serialize_i16`, `serialize_i32`, `serialize_i64`, `serialize_i128`, `serialize_u8`, `serialize_u16`, `serialize_u32`, `serialize_u64`, `serialize_u128`, `serialize_f32`, `serialize_f64`, `serialize_char`, `serialize_str`, `serialize_bytes`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_none`, `serialize_some`, `serialize_seq`, `serialize_tuple`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `collect_str`, `is_human_readable`, `serialize_field`, `end`, `deserialize_any`, `deserialize_bool`, `deserialize_i8`, `deserialize_i16`, `deserialize_i32`, `deserialize_i64`, `deserialize_i128`, `deserialize_u8`, `deserialize_u16`, `deserialize_u32`, `deserialize_u64`, `deserialize_u128`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_enum`, `deserialize_identifier`, `deserialize_ignored_any`, `expecting`, `visit_str`, `visit_borrowed_str`, `visit_string`, `visit_none`, `visit_some`, `visit_unit`, `visit_map`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `serialize_element`, `serialize_key`, `serialize_value`, `serialize_entry`, `visit_bool`, `visit_i8`, `visit_i16`, `visit_i32`, `visit_i64`, `visit_i128`, `visit_u8`, `visit_u16`, `visit_u32`, `visit_u64`, `visit_u128`, `visit_f32`, `visit_f64`, `visit_char`, `visit_bytes`, `visit_borrowed_bytes`, `visit_byte_buf`, `visit_newtype_struct`, `visit_seq`, `next_element_seed`, `next_key_seed`, `next_value_seed`
- **Types:** 2/21 matched (target 3)
- **Missing types:** `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `SerializeTupleVariantAsSingletonMap`, `SerializeStructVariantAsSingletonMap`, `SingletonMapAsEnum`, `Value`, `Variant`, `TupleVariantSeed`, `StructVariantSeed`, `SerializeTupleVariantAsSingletonMapRecursive`, `SerializeStructVariantAsSingletonMapRecursive`, `SingletonMapRecursiveAsEnum`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/with.rs` vs expected `with.rs`
- **Proposed provenance header:** `// port-lint: source with.rs` (current: `// port-lint: source serde_yaml/src/with.rs`)
- **Lint issues:** 1

### 8. number

- **Target:** `serdeyaml.Number [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1073010.0
- **Functions:** 20/24 matched (target 49)
- **Missing functions:** `serialize`, `deserialize`, `deserialize_any`, `unexpected`
- **Types:** 3/6 matched
- **Missing types:** `Err`, `Value`, `Error`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/number.rs` vs expected `number.rs`
- **Proposed provenance header:** `// port-lint: source number.rs` (current: `// port-lint: source serde_yaml/src/number.rs`)
- **Lint issues:** 1

### 9. value.index

- **Target:** `value.Index [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1011210.0
- **Functions:** 9/9 matched (target 16)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 5)
- **Missing types:** `Output`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/index.rs` vs expected `value/index.rs`
- **Proposed provenance header:** `// port-lint: source value/index.rs` (current: `// port-lint: source serde_yaml/src/value/index.rs`)
- **Lint issues:** 1

### 10. ser

- **Target:** `serdeyaml.Ser [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 677410.0
- **Functions:** 5/60 matched (target 23)
- **Missing functions:** `new`, `flush`, `into_inner`, `emit_scalar`, `emit_sequence_start`, `emit_sequence_end`, `emit_mapping_start`, `emit_mapping_end`, `value_start`, `value_end`, `take_tag`, `flush_mapping_start`, `serialize_bool`, `serialize_i8`, `serialize_i16`, `serialize_i32`, `serialize_i64`, `serialize_i128`, `serialize_u8`, `serialize_u16`, `serialize_u32`, `serialize_u64`, `serialize_u128`, `serialize_f32`, `serialize_f64`, `serialize_char`, `serialize_str`, `expecting`, `visit_bool`, `visit_i64`, `visit_i128`, `visit_u64`, `visit_u128`, `visit_f64`, `visit_str`, `visit_unit`, `serialize_bytes`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_none`, `serialize_some`, `serialize_seq`, `serialize_tuple`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `collect_str`, `serialize_element`, `serialize_key`, `serialize_value`
- **Types:** 2/14 matched (target 5)
- **Missing types:** `Result`, `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `InferScalarStyle`, `Value`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/ser.rs` vs expected `ser.rs`
- **Proposed provenance header:** `// port-lint: source ser.rs` (current: `// port-lint: source serde_yaml/src/ser.rs`)
- **Lint issues:** 1

### 11. value.de

- **Target:** `value.De [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 377410.0
- **Functions:** 37/62 matched (target 43)
- **Missing functions:** `deserialize`, `expecting`, `visit_bool`, `visit_i64`, `visit_u64`, `visit_f64`, `visit_str`, `visit_string`, `visit_unit`, `visit_none`, `visit_some`, `visit_seq`, `visit_map`, `visit_enum`, `deserialize_number`, `visit_sequence`, `visit_sequence_ref`, `visit_mapping`, `visit_mapping_ref`, `deserialize_i128`, `deserialize_u128`, `deserialize_identifier`, `new`, `invalid_type`, `unexpected`
- **Types:** 0/12 matched (target 7)
- **Missing types:** `ValueVisitor`, `Value`, `Error`, `EnumDeserializer`, `Variant`, `VariantDeserializer`, `SeqDeserializer`, `MapDeserializer`, `EnumRefDeserializer`, `VariantRefDeserializer`, `SeqRefDeserializer`, `MapRefDeserializer`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/de.rs` vs expected `value/de.rs`
- **Proposed provenance header:** `// port-lint: source value/de.rs` (current: `// port-lint: source serde_yaml/src/value/de.rs`)
- **Lint issues:** 1

### 12. value.tagged

- **Target:** `value.Tagged [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 213510.0
- **Functions:** 10/25 matched (target 24)
- **Missing functions:** `untag`, `untag_ref`, `untag_mut`, `serialize`, `deserialize`, `visit_enum`, `deserialize_any`, `deserialize_ignored_any`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `visit_string`, `write_str`
- **Types:** 4/10 matched (target 7)
- **Missing types:** `SerializeTag`, `Value`, `Error`, `Variant`, `TagStringVisitor`, `CheckForTag`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/tagged.rs` vs expected `value/tagged.rs`
- **Proposed provenance header:** `// port-lint: source value/tagged.rs` (current: `// port-lint: source serde_yaml/src/value/tagged.rs`)
- **Lint issues:** 1

### 13. value.ser

- **Target:** `value.Ser [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 105210.0
- **Functions:** 34/38 matched (target 45)
- **Missing functions:** `serialize`, `serialize_i128`, `serialize_u128`, `collect_str`
- **Types:** 8/14 matched (target 8)
- **Missing types:** `Result`, `Ok`, `Error`, `SerializeArray`, `CheckForTag`, `NotTag`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/ser.rs` vs expected `value/ser.rs`
- **Proposed provenance header:** `// port-lint: source value/ser.rs` (current: `// port-lint: source serde_yaml/src/value/ser.rs`)
- **Lint issues:** 1

### 14. libyaml.emitter

- **Target:** `libyaml.Emitter [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 61510.0
- **Functions:** 6/7 matched (target 8)
- **Missing functions:** `into_inner`
- **Types:** 3/8 matched (target 19)
- **Missing types:** `Error`, `Event`, `ScalarStyle`, `Sequence`, `Mapping`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/libyaml/emitter.rs` vs expected `libyaml/emitter.rs`
- **Proposed provenance header:** `// port-lint: source libyaml/emitter.rs` (current: `// port-lint: source serde_yaml/src/libyaml/emitter.rs`)
- **Lint issues:** 1

### 15. libyaml.error

- **Target:** `libyaml.Error [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31010.0
- **Functions:** 4/7 matched (target 9)
- **Missing functions:** `index`, `line`, `column`
- **Types:** 3/3 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/libyaml/error.rs` vs expected `libyaml/error.rs`
- **Proposed provenance header:** `// port-lint: source libyaml/error.rs` (current: `// port-lint: source serde_yaml/src/libyaml/error.rs`)
- **Lint issues:** 1

### 16. value.mod

- **Target:** `serdeyaml.Value [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 13010.0
- **Functions:** 27/27 matched (target 86)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 8)
- **Missing types:** `Deserializer`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/mod.rs` vs expected `value/mod.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/mod.rs` vs expected `value/mod.rs`
- **Proposed provenance header:** `// port-lint: source value/mod.rs` (current: `// port-lint: source serde_yaml/src/value/mod.rs`)
- **Proposed provenance header:** `// port-lint: source value/mod.rs` (current: `// port-lint: source serde_yaml/src/value/mod.rs`)
- **Lint issues:** 2

### 17. libyaml.util

- **Target:** `libyaml.Util [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10710.0
- **Functions:** 4/4 matched (target 8)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 2)
- **Missing types:** `Target`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/libyaml/util.rs` vs expected `libyaml/util.rs`
- **Proposed provenance header:** `// port-lint: source libyaml/util.rs` (current: `// port-lint: source serde_yaml/src/libyaml/util.rs`)
- **Lint issues:** 1

### 18. value.debug

- **Target:** `value.Debug [PROVENANCE-FALLBACK]`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 10207.6
- **Functions:** 1/1 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 0)
- **Missing types:** `DisplayNumber`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/debug.rs` vs expected `value/debug.rs`
- **Proposed provenance header:** `// port-lint: source value/debug.rs` (current: `// port-lint: source serde_yaml/src/value/debug.rs`)
- **Lint issues:** 1

### 19. libyaml.parser

- **Target:** `libyaml.Parser [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1610.0
- **Functions:** 7/7 matched (target 29)
- **Missing functions:** _none_
- **Types:** 9/9 matched (target 29)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/libyaml/parser.rs` vs expected `libyaml/parser.rs`
- **Proposed provenance header:** `// port-lint: source libyaml/parser.rs` (current: `// port-lint: source serde_yaml/src/libyaml/parser.rs`)
- **Lint issues:** 1

### 20. loader

- **Target:** `serdeyaml.Loader [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 410.0
- **Functions:** 2/2 matched (target 23)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/loader.rs` vs expected `loader.rs`
- **Proposed provenance header:** `// port-lint: source loader.rs` (current: `// port-lint: source serde_yaml/src/loader.rs`)
- **Lint issues:** 1

### 21. value.from

- **Target:** `value.From [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 210.0
- **Functions:** 2/2 matched (target 17)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/from.rs` vs expected `value/from.rs`
- **Proposed provenance header:** `// port-lint: source value/from.rs` (current: `// port-lint: source serde_yaml/src/value/from.rs`)
- **Lint issues:** 1

### 22. value.partial_eq

- **Target:** `value.PartialEq [PROVENANCE-FALLBACK]`
- **Similarity:** 0.80
- **Dependents:** 0
- **Priority Score:** 102.0
- **Functions:** 1/1 matched (target 11)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `serde_yaml/src/value/partial_eq.rs` vs expected `value/partial_eq.rs`
- **Proposed provenance header:** `// port-lint: source value/partial_eq.rs` (current: `// port-lint: source serde_yaml/src/value/partial_eq.rs`)
- **Lint issues:** 1

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Matched

| Source | Target | Path |
|--------|--------|------|
| `lib` | `serdeyaml.Lib` | `lib` |
| `libyaml.mod` | `libyaml.Mod` | `libyaml/mod` |

