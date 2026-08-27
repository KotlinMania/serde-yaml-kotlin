# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 24/24 (100.0%)
- **Function parity:** 229/538 matched (target 555) — 42.6%
- **Class/type parity:** 60/159 matched (target 163) — 37.7%
- **Combined symbol parity:** 289/697 matched (target 718) — 41.5%
- **Average inline-code cosine:** 0.08 (function body across 21 matched files)
- **Average documentation cosine:** 0.14 (doc text across 21 matched files)
- **Cheat-zeroed Files:** 20
- **Critical Issues:** 23 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. mapping

- **Target:** `serdeyaml.Mapping [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 3
- **Priority Score:** 3437510.0
- **Functions:** 26/56 matched (target 71)
- **Missing functions:** `keys`, `values`, `values_mut`, `equivalent`, `hash`, `is_key_into`, `index_into`, `index_into_mut`, `swap_remove_from`, `swap_remove_entry_from`, `shift_remove_from`, `shift_remove_entry_from`, `partial_cmp`, `total_cmp`, `iter_cmp_by`, `index`, `index_mut`, `extend`, `from_iter`, `into_iter`, `or_insert_with`, `and_modify`, `into_mut`, `into_key`, `serialize`, `deserialize`, `expecting`, `visit_unit`, `visit_map`, `fmt`
- **Types:** 6/19 matched (target 8)
- **Missing types:** `Index`, `Output`, `Iter`, `Item`, `IntoIter`, `IterMut`, `Keys`, `IntoKeys`, `Values`, `ValuesMut`, `IntoValues`, `Visitor`, `Value`
- **Lint issues:** 1

### 2. libyaml.tag

- **Target:** `libyaml.Tag`
- **Similarity:** 0.51
- **Dependents:** 2
- **Priority Score:** 2010605.0
- **Functions:** 4/4 matched (target 12)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`

### 3. error

- **Target:** `serdeyaml.Error [STUB]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2002210.0
- **Functions:** 16/16 matched (target 23)
- **Missing functions:** _none_
- **Types:** 6/6 matched (target 24)
- **Missing types:** _none_

### 4. libyaml.cstr

- **Target:** `libyaml.Cstr [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 2
- **Priority Score:** 2000810.0
- **Functions:** 7/7 matched (target 14)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 5. path

- **Target:** `serdeyaml.Path`
- **Similarity:** 0.12
- **Dependents:** 2
- **Priority Score:** 2000308.8
- **Functions:** 1/1 matched (target 6)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 7)
- **Missing types:** _none_

### 6. de

- **Target:** `serdeyaml.De [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1859110.0
- **Functions:** 4/72 matched (target 21)
- **Missing functions:** `de`, `deserialize_any`, `deserialize_bool`, `deserialize_i8`, `deserialize_i16`, `deserialize_i32`, `deserialize_i64`, `deserialize_i128`, `deserialize_u8`, `deserialize_u16`, `deserialize_u32`, `deserialize_u64`, `deserialize_u128`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_enum`, `deserialize_identifier`, `deserialize_ignored_any`, `peek_event`, `peek_event_mark`, `next_event`, `next_event_mark`, `jump`, `ignore_any`, `visit_sequence`, `visit_mapping`, `end_sequence`, `fmt`, `end_mapping`, `recursion_check`, `next_element_seed`, `next_key_seed`, `next_value_seed`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `visit_scalar`, `parse_borrowed_str`, `parse_null`, `parse_bool`, `parse_unsigned_int`, `parse_signed_int`, `parse_negative_int`, `parse_f64`, `digits_but_not_number`, `visit_int`, `visit_untagged_scalar`, `is_plain_or_tagged_literal_scalar`, `invalid_type`, `expecting`, `parse_tag`, `enum_tag`
- **Types:** 2/19 matched (target 3)
- **Missing types:** `Result`, `Item`, `Error`, `Event`, `DeserializerFromEvents`, `CurrentEnum`, `Nest`, `ExpectedSeq`, `ExpectedMap`, `SeqAccess`, `MapAccess`, `EnumAccess`, `Variant`, `UnitVariantAccess`, `Void`, `InvalidType`, `Value`

### 7. with

- **Target:** `serdeyaml.With [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1242710.0
- **Functions:** 2/106 matched (target 8)
- **Missing functions:** `serialize_bool`, `serialize_i8`, `serialize_i16`, `serialize_i32`, `serialize_i64`, `serialize_i128`, `serialize_u8`, `serialize_u16`, `serialize_u32`, `serialize_u64`, `serialize_u128`, `serialize_f32`, `serialize_f64`, `serialize_char`, `serialize_str`, `serialize_bytes`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_none`, `serialize_some`, `serialize_seq`, `serialize_tuple`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `collect_str`, `is_human_readable`, `serialize_field`, `end`, `deserialize_any`, `deserialize_bool`, `deserialize_i8`, `deserialize_i16`, `deserialize_i32`, `deserialize_i64`, `deserialize_i128`, `deserialize_u8`, `deserialize_u16`, `deserialize_u32`, `deserialize_u64`, `deserialize_u128`, `deserialize_f32`, `deserialize_f64`, `deserialize_char`, `deserialize_str`, `deserialize_string`, `deserialize_bytes`, `deserialize_byte_buf`, `deserialize_option`, `deserialize_unit`, `deserialize_unit_struct`, `deserialize_newtype_struct`, `deserialize_seq`, `deserialize_tuple`, `deserialize_tuple_struct`, `deserialize_map`, `deserialize_struct`, `deserialize_enum`, `deserialize_identifier`, `deserialize_ignored_any`, `expecting`, `visit_str`, `visit_borrowed_str`, `visit_string`, `visit_none`, `visit_some`, `visit_unit`, `visit_map`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `serialize_element`, `serialize_key`, `serialize_value`, `serialize_entry`, `visit_bool`, `visit_i8`, `visit_i16`, `visit_i32`, `visit_i64`, `visit_i128`, `visit_u8`, `visit_u16`, `visit_u32`, `visit_u64`, `visit_u128`, `visit_f32`, `visit_f64`, `visit_char`, `visit_bytes`, `visit_borrowed_bytes`, `visit_byte_buf`, `visit_newtype_struct`, `visit_seq`, `next_element_seed`, `next_key_seed`, `next_value_seed`
- **Types:** 2/21 matched (target 3)
- **Missing types:** `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `SerializeTupleVariantAsSingletonMap`, `SerializeStructVariantAsSingletonMap`, `SingletonMapAsEnum`, `Value`, `Variant`, `TupleVariantSeed`, `StructVariantSeed`, `SerializeTupleVariantAsSingletonMapRecursive`, `SerializeStructVariantAsSingletonMapRecursive`, `SingletonMapRecursiveAsEnum`

### 8. number

- **Target:** `serdeyaml.Number [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1073010.0
- **Functions:** 20/24 matched (target 49)
- **Missing functions:** `serialize`, `deserialize`, `deserialize_any`, `unexpected`
- **Types:** 3/6 matched
- **Missing types:** `Err`, `Value`, `Error`

### 9. value.index

- **Target:** `value.Index [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 1
- **Priority Score:** 1011210.0
- **Functions:** 9/9 matched (target 16)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 5)
- **Missing types:** `Output`

### 10. ser

- **Target:** `serdeyaml.Ser [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 677410.0
- **Functions:** 5/60 matched (target 23)
- **Missing functions:** `new`, `flush`, `into_inner`, `emit_scalar`, `emit_sequence_start`, `emit_sequence_end`, `emit_mapping_start`, `emit_mapping_end`, `value_start`, `value_end`, `take_tag`, `flush_mapping_start`, `serialize_bool`, `serialize_i8`, `serialize_i16`, `serialize_i32`, `serialize_i64`, `serialize_i128`, `serialize_u8`, `serialize_u16`, `serialize_u32`, `serialize_u64`, `serialize_u128`, `serialize_f32`, `serialize_f64`, `serialize_char`, `serialize_str`, `expecting`, `visit_bool`, `visit_i64`, `visit_i128`, `visit_u64`, `visit_u128`, `visit_f64`, `visit_str`, `visit_unit`, `serialize_bytes`, `serialize_unit`, `serialize_unit_struct`, `serialize_unit_variant`, `serialize_newtype_struct`, `serialize_newtype_variant`, `serialize_none`, `serialize_some`, `serialize_seq`, `serialize_tuple`, `serialize_tuple_struct`, `serialize_tuple_variant`, `serialize_map`, `serialize_struct`, `serialize_struct_variant`, `collect_str`, `serialize_element`, `serialize_key`, `serialize_value`
- **Types:** 2/14 matched (target 5)
- **Missing types:** `Result`, `Ok`, `Error`, `SerializeSeq`, `SerializeTuple`, `SerializeTupleStruct`, `SerializeTupleVariant`, `SerializeMap`, `SerializeStruct`, `SerializeStructVariant`, `InferScalarStyle`, `Value`

### 11. value.de

- **Target:** `value.De [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 377410.0
- **Functions:** 37/62 matched (target 43)
- **Missing functions:** `deserialize`, `expecting`, `visit_bool`, `visit_i64`, `visit_u64`, `visit_f64`, `visit_str`, `visit_string`, `visit_unit`, `visit_none`, `visit_some`, `visit_seq`, `visit_map`, `visit_enum`, `deserialize_number`, `visit_sequence`, `visit_sequence_ref`, `visit_mapping`, `visit_mapping_ref`, `deserialize_i128`, `deserialize_u128`, `deserialize_identifier`, `new`, `invalid_type`, `unexpected`
- **Types:** 0/12 matched (target 7)
- **Missing types:** `ValueVisitor`, `Value`, `Error`, `EnumDeserializer`, `Variant`, `VariantDeserializer`, `SeqDeserializer`, `MapDeserializer`, `EnumRefDeserializer`, `VariantRefDeserializer`, `SeqRefDeserializer`, `MapRefDeserializer`

### 12. value.tagged

- **Target:** `value.Tagged [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 213510.0
- **Functions:** 10/25 matched (target 24)
- **Missing functions:** `untag`, `untag_ref`, `untag_mut`, `serialize`, `deserialize`, `visit_enum`, `deserialize_any`, `deserialize_ignored_any`, `variant_seed`, `unit_variant`, `newtype_variant_seed`, `tuple_variant`, `struct_variant`, `visit_string`, `write_str`
- **Types:** 4/10 matched (target 7)
- **Missing types:** `SerializeTag`, `Value`, `Error`, `Variant`, `TagStringVisitor`, `CheckForTag`

### 13. value.ser

- **Target:** `value.Ser [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 105210.0
- **Functions:** 34/38 matched (target 45)
- **Missing functions:** `serialize`, `serialize_i128`, `serialize_u128`, `collect_str`
- **Types:** 8/14 matched (target 8)
- **Missing types:** `Result`, `Ok`, `Error`, `SerializeArray`, `CheckForTag`, `NotTag`
- **Lint issues:** 2

### 14. libyaml.emitter

- **Target:** `libyaml.Emitter [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 61510.0
- **Functions:** 6/7 matched (target 8)
- **Missing functions:** `into_inner`
- **Types:** 3/8 matched (target 19)
- **Missing types:** `Error`, `Event`, `ScalarStyle`, `Sequence`, `Mapping`
- **Lint issues:** 3

### 15. libyaml.error

- **Target:** `libyaml.Error [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31010.0
- **Functions:** 4/7 matched (target 9)
- **Missing functions:** `index`, `line`, `column`
- **Types:** 3/3 matched
- **Missing types:** _none_

### 16. value.mod

- **Target:** `serdeyaml.Value [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 13010.0
- **Functions:** 27/27 matched (target 86)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 8)
- **Missing types:** `Deserializer`

### 17. libyaml.util

- **Target:** `libyaml.Util [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10710.0
- **Functions:** 4/4 matched (target 8)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 2)
- **Missing types:** `Target`

### 18. value.debug

- **Target:** `value.Debug`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 10207.6
- **Functions:** 1/1 matched (target 6)
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 0)
- **Missing types:** `DisplayNumber`

### 19. lib

- **Target:** `serdeyaml.Lib [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10110.0
- **Functions:** 0/0 matched (target 3)
- **Missing functions:** _none_
- **Types:** 0/1 matched (target 2)
- **Missing types:** `Sealed`

### 20. libyaml.parser

- **Target:** `libyaml.Parser [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 1610.0
- **Functions:** 7/7 matched (target 29)
- **Missing functions:** _none_
- **Types:** 9/9 matched (target 29)
- **Missing types:** _none_

### 21. loader

- **Target:** `serdeyaml.Loader [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 410.0
- **Functions:** 2/2 matched (target 23)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_

### 22. value.from

- **Target:** `value.From [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 210.0
- **Functions:** 2/2 matched (target 17)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 23. value.partial_eq

- **Target:** `value.PartialEq`
- **Similarity:** 0.80
- **Dependents:** 0
- **Priority Score:** 102.0
- **Functions:** 1/1 matched (target 11)
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 24. libyaml.mod

- **Target:** `libyaml.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 10.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 12)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

