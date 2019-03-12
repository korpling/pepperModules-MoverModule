![SaltNPepper project](./gh-site/img/SaltNPepper_logo2010.png)
# MoverModule

This project provides a manipulator to move annotations in a document from one object to another, for example to move an edge annotation from the edge to a source or target node, or from a node to another node.

This module is implemented for the linguistic converter framework Pepper (see https://u.hu-berlin.de/saltnpepper). A detailed description of the manipulator can be found in [Mover](#details1) or in a [poster](http://dx.doi.org/10.5281/zenodo.15640) presented at the DGfS 2014.

Pepper is a pluggable framework to convert a variety of linguistic formats (like [TigerXML](http://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/TIGERSearch/doc/html/TigerXML.html), the [EXMARaLDA format](http://www.exmaralda.org/), [PAULA](http://www.sfb632.uni-potsdam.de/paula.html) etc.) into each other. Furthermore Pepper uses Salt (see https://github.com/korpling/salt), the graph-based meta model for linguistic data, which acts as an intermediate model to reduce the number of mappings to be implemented. That means converting data from a format _A_ to format _B_ consists of two steps. First the data is mapped from format _A_ to Salt and second from Salt to format _B_. This detour reduces the number of Pepper modules from _n<sup>2</sup>-n_ (in the case of a direct mapping) to _2n_ to handle a number of n formats.

![n:n mappings via SaltNPepper](./gh-site/img/puzzle.png)

In Pepper there are three different types of modules:
* importers (to map a format _A_ to a Salt model)
* manipulators (to map a Salt model to a Salt model, e.g. to add additional annotations, to rename things to merge data etc.)
* exporters (to map a Salt model to a format _B_).

For a simple Pepper workflow you need at least one importer and one exporter.

## Requirements
Since the module provided here is a plugin for Pepper, you need an instance of the Pepper framework. If you do not already have a running Pepper instance, click on the link below and download the latest stable version (not a SNAPSHOT):

> Note:
> Pepper is a Java based program, therefore you need to have at least Java 7 (JRE or JDK) on your system. You can download Java from https://www.oracle.com/java/index.html or http://openjdk.java.net/ .


## Install module
If this Pepper module is not yet contained in your Pepper distribution, you can easily install it. Just open a command line and enter one of the following program calls:

**Windows**
```
pepperStart.bat
```

**Linux/Unix**
```
bash pepperStart.sh
```

Then type in command *is* and the path from where to install the module:
```
pepper> update de.hu_berlin.german.korpling.saltnpepper::pepperModules-MergingModules::https://korpling.german.hu-berlin.de/maven2/
```

## Usage
To use this module in your Pepper workflow, put the following lines into the workflow description file. Note the fixed order of xml elements in the workflow description file: &lt;importer/>, &lt;manipulator/>, &lt;exporter/>. The Mover is a manipulator module, which can be addressed by one of the following alternatives.
A detailed description of the Pepper workflow can be found on the [Pepper project site](https://u.hu-berlin.de/saltnpepper).

### a) Identify the module by name

```xml
<manipulator name="Mover"/>
```

### b) Use properties

```xml
<manipulator name="Mover">
  <property key="PROPERTY_NAME">PROPERTY_VALUE</property>
</manipulator>
```

## Contribute
Since this Pepper module is under a free license, please feel free to fork it from github and improve the module. If you even think that others can benefit from your improvements, don't hesitate to make a pull request, so that your changes can be merged.
If you have found any bugs, or have some feature request, please open an issue on github. If you need any help, please write an e-mail to saltnpepper@lists.hu-berlin.de .

## Funders
This project module was developed at Georgetown University.

## License
  Copyright 2017-2019 Amir Zeldes, Georgetown University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.


# <a name="details1">Mover</a>
The Mover adds token number annotations to tokens and optionally a metadatum with the total count of tokens. The Mover can be integrated in a Pepper workflow as a step of the manipulation phase.

## Properties
The manipulation process can be customized by using the properties listed in the following table.

|name of property			|possible values		|default value|
|---------------------------|-----------------------|-------------|
|Mover.annoNamespace			    |String	                |'default_ns'|
|Mover.annoName			    |String	                |'tok_num'|
|Mover.metaAnnoNamespace			    |String	                |NULL|
|Mover.metaAnnoName			    |String	                |'tok_count'|
|Mover.noCountAsMeta				    |true, false			|false|
|Mover.noTokenNumbers	            |true, false			|false|

### Mover.annoNamespace
Specifies a namespace to add to token number annotations. (default: default_ns)

### Mover.annoName
Specifies the annotation name for token numbers. (default: tok_num)

### Mover.metaAnnoNamespace
Specifies a namespace to add to token count meta annotation. (default: NULL)

### Mover.metaAnnoName
Specifies the annotation name for token count meta annotation. (default: tok_count)

### Mover.noCountAsMeta
Whether to leave out the metadatum with the count of tokens to each document. (default: false)

### Mover.noTokenNumbers
If true, then tokens are not numbered (for cases where you only want metadata added). (default: false)

## Example workflows

Here are some useful example scenarios for using this module.

Copy a parent lemma from a dependency parent to a dependency child:

```
<manipulator name="Mover">
  <property key="Mover.sourceType">source2target</property>
  <property key="Mover.sourceName">dep</property>
  <property key="Mover.sourceAnno">lemma</property>
  <property key="Mover.sourceAnnoNamespace">default_ns</property>
  <property key="Mover.targetAnno">parent_lemma</property>
  <property key="Mover.targetObject">target</property>
</manipulator>
```

Insert a fixed node annotation `anaphoric=anaphor` for any node with an outgoing `coref` edge:

```
<manipulator name="Mover">
  <property key="Mover.sourceName">coref</property>
  <property key="Mover.sourceLayer">coref</property>
  <property key="Mover.targetNamespace">coref</property>
  <property key="Mover.targetAnno">anaphoric</property>
  <property key="Mover.targetAnnoVal">anaphor</property>
  <property key="Mover.targetObject">source</property>
</manipulator>
```

Move a morphological token annotation to a span above that token:

```
<manipulator name="Mover">
  <property key="Mover.sourceType">tok2span</property>
  <property key="Mover.sourceName">morph</property> <!-- this is  reserved value identifying tokens -->
  <property key="Mover.targetNamespace">morph_span</property> <!-- if unused, target NS will also be 'morph' -->
  <property key="Mover.targetAnno">morph_span</property> <!-- if unused, target annotation will also be 'morph' -->
  <property key="Mover.targetObject">span</property> <!-- can also use 'struct' -->
</manipulator>
```
