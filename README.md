# camel-visualizer
Visualize an Apache Camel context with PlantUML diagram. Run following ...

```Java
FileCamelDiagramExporter.fileExporter(camelContext, "sourceFilePath.plantuml", "imageFilePath.png").export();
```

... to generate PlantUML code and PNG image.

**Note**: to generate PNG image sucessfully you need to have Graphviz installed and set GRAPHVIZ_DOT to point to the dot executable.
