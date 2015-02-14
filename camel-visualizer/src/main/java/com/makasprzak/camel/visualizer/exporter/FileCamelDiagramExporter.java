package com.makasprzak.camel.visualizer.exporter;

import com.makasprzak.camel.visualizer.CamelVisualizer;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.camel.model.ModelCamelContext;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCamelDiagramExporter implements CamelDiagramExporter {
    private static final Logger LOG = LoggerFactory.getLogger(FileCamelDiagramExporter.class);
    private final CamelVisualizer camelVisualizer;
    private final String path;
    private final String sourcePath;

    public FileCamelDiagramExporter(String path, String sourcePath, ModelCamelContext camelContext) {
        this(new CamelVisualizer(camelContext), path, sourcePath);
    }

    FileCamelDiagramExporter(CamelVisualizer camelVisualizer, String imagePath, String sourcePath) {
        this.camelVisualizer = camelVisualizer;
        this.path = imagePath;
        this.sourcePath = sourcePath;
    }

    @Override
    public void export() {
        String plantUML = camelVisualizer.toPlantUML();
        writeImage(plantUML);
        writeSource(plantUML);
    }

    private void writeSource(String plantUML) {
        try {
            FileUtils.write(getOrCreateFile(sourcePath), plantUML, (String) null);
        } catch (IOException e) {
            LOG.error("Failed to write source.",e);
        }
    }

    private void writeImage(String plantUML) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getOrCreateFile(path));
            SourceStringReader sourceStringReader = new SourceStringReader(plantUML);
            LOG.info(sourceStringReader.generateImage(fos));
            LOG.info("Diagram successfully exported");
        } catch (IOException e) {
            LOG.error("Failed to generate the diagram!",e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOG.error("Failed to close output file!",e);
                }
            }
        }
    }

    private File getOrCreateFile(String path) throws IOException {
        File outputFile = new File(path);
        if (!outputFile.createNewFile()) {
            LOG.info("The specified output file ({}) already exists and will be overwritten.", path);
        } else {
            LOG.debug("Created new file {}", path);
        }
        return outputFile;
    }

    public static CamelDiagramExporter fileExporter(ModelCamelContext context, String sourcePath, String imagePath) {
        return new FileCamelDiagramExporter(imagePath, sourcePath, context);
    }
}
