package io.github.kotlinmania.serdeyaml.libyaml

// port-lint: source serde_yaml/src/libyaml/mod.rs

public typealias LibyamlError = io.github.kotlinmania.serdeyaml.libyaml.Error
public typealias LibyamlMark = io.github.kotlinmania.serdeyaml.libyaml.Mark
public typealias LibyamlTag = io.github.kotlinmania.serdeyaml.libyaml.Tag
public typealias LibyamlCstr = io.github.kotlinmania.serdeyaml.libyaml.Cstr
public typealias LibyamlParser = io.github.kotlinmania.serdeyaml.libyaml.Parser
public typealias LibyamlEmitter = io.github.kotlinmania.serdeyaml.libyaml.Emitter

public object cstr

public object emitter

public object error

public object parser

public object tag

public object util
