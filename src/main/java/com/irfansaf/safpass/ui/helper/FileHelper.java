package com.irfansaf.safpass.ui.helper;

import com.irfansaf.safpass.data.EntriesRepository;
import com.irfansaf.safpass.ui.SafPassFrame;
import com.irfansaf.safpass.ui.action.Worker;
import com.irfansaf.safpass.util.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import static com.irfansaf.safpass.ui.MessageDialog.*;
import static com.irfansaf.safpass.util.StringUtils.stripString;
import static java.lang.CharSequence.compare;
import static java.lang.String.format;

public final class FileHelper {


    private static final String FILE_OVERWRITE_QUESTION_MESSAGE
            = "The current file has been modified.\n"
            + "Do you want to save the changes before closing?";
    private static final String CREATE_FILE_QUESTION_MESSAGE
            = "File not found:\n%s\n\nDo you want to create the file?";
    private static final String SAVE_MODIFIED_QUESTOON_MESSAGE
            = "The current file has been modified.\n"
            + "Do you want to save the changes before closing?";
    private static final String UNENCRYPTED_DATA_WARNING_MESSAGE
            = "Please note that all data will be stored unencrypted.\n"
            + "Make sure you keep the exported file in a secure location.";
    private static final String OPERATION_ERROR_MESSAGE
            = "An error occurred during the %s operation:\n%s";
    private static final String OPEN_ERROR_CHECK_PASSWORD_ERROR_MESSAGE
            = "An error occurred during the open opeartion.\nThe password might be incorrected.\n(Error: %s)";


    private static final String SAFPASS_DATA_FILES = "SafPass Data Files (*.safpass)";
    private static final String XML_FILES = "XML Files (*.xml)";

    private FileHelper() {
        // Not intended to be instantiated
    }

    /**
     * LLoads a file and fills the data model.
     *
     * @param fileName
     * @param parent
     */
    public static void openFileInBackground(final String fileName, final SafPassFrame parent) {
        parent.clearModel();
        if (fileName == null) {
            return;
        }
        final char[] password = showPasswordDialog(parent, false);
        if (password == null) {
            return;
        }
        Worker worker = new Worker(parent) {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    parent.getModel().setEntries(EntriesRepository.newInstance(fileName).readDocument());
                    parent.getModel().setFileName(fileName);
                    parent.getModel().setPassword(password);
                    parent.getSearchPanel().setVisible(false);
                } catch (FileNotFoundException e) {
                    throw e;
                } catch (IOException e) {
                    throw new Exception(format(OPEN_ERROR_CHECK_PASSWORD_ERROR_MESSAGE, stripString(e.getMessage())));
                } catch (Throwable e) {
                    throw new Exception(format(OPERATION_ERROR_MESSAGE, "open", e.getMessage()));
                }
                return null;
            }

            @Override
            protected void done() {
                stopProcessing();
                try {
                    get();
                } catch (Exception e) {
                    if (e.getCause() instanceof FileNotFoundException) {
                        handleFileNotFound(parent, fileName, password);
                    } else {
                        showErrorMessage(e);
                    }
                }
            }
        };
        worker.execute();
    }

    /**
     * Handles file not found exception;
     *
     * @param parent frame
     * @param fileName
     * @param password password to create a new file
     */
    static void handleFileNotFound(final SafPassFrame parent, final String fileName, final char[] password) {
        int option = showQuestionMessage(parent, format(CREATE_FILE_QUESTION_MESSAGE, stripString(fileName)), YES_NO_OPTION);
        if (option == YES_OPTION) {
            Worker fileNotFoundWorker = new Worker(parent) {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        EntriesRepository.newInstance(fileName, password).writeDocument(parent.getModel().getEntries());
                        parent.getModel().setFileName(fileName);
                        parent.getModel().setPassword(password);
                    } catch (Exception ex) {
                        throw new Exception(format(OPERATION_ERROR_MESSAGE, "open", ex.getMessage()));
                    }
                    return null;
                }
            };
            fileNotFoundWorker.execute();
        }
    }

    /**
     * Shows a file chooser dialog.
     *
     * @param parent component
     * @param taskName name of the task
     * @param extension accepted file extension
     * @param description file extension description
     * @return a file object
     */
    private static File showFileChooser(SafPassFrame parent, String taskName, String extension, String description) {
        File ret = null;
        String fileChooserDir = Configuration.getInstance().get("file.chooser.directory", "./");
        JFileChooser fc = new JFileChooser(fileChooserDir.isEmpty() ? null : fileChooserDir);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith("." + extension);
            }

            @Override
            public String getDescription() {
                return description;
            }
        });
        int returnVal = fc.showDialog(parent, taskName);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ret = fc.getSelectedFile();
        }
        return ret;
    }

    /**
     * Checks if overwrite is accepted
     *
     * @param fileName filename
     * @param parent parent component
     * @return {@code true} if overwrite is accepted; otherwise {@code false}
     */
    private static boolean checkFileOverwrite(String fileName, SafPassFrame parent) {
        boolean overWriteAccepted = true;
        File file = new File(fileName);
        if (file.exists()) {
            int option = showQuestionMessage(parent, format(FILE_OVERWRITE_QUESTION_MESSAGE, stripString(fileName)), YES_NO_OPTION);
            if (option != YES_OPTION) {
                overWriteAccepted = false;
            }
        }
        return overWriteAccepted;
    }

    /**
     * Checks if the filename has the given extension
     *
     * @param fileName filename
     * @param extension extension
     * @return filename ending with the given extension
     */
    private static String checkExtension(final String fileName, final String extension) {
        String separator = fileName.endsWith(".") ? "" : ".";
        if (!fileName.toLowerCase().endsWith(separator + extension)) {
            return fileName + separator + extension;
        }
        return fileName;
    }
}
