package main;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import java.io.File;
import java.io.IOException;

public class FileManager implements FileTransferListener {
    @Override
    public void fileTransferRequest(FileTransferRequest fileTransferRequest) {
        try {
            File file = new File("./");
            IncomingFileTransfer transfer = fileTransferRequest.accept();
            transfer.receiveFile(file);
            String sender = fileTransferRequest.getRequestor().asBareJid().toString();
            String file_name = fileTransferRequest.getFileName();
            OutputManager.getInstance().print("-> "+sender+" sent you "+file_name);
            fileTransferRequest.accept();
        } catch (SmackException | IOException e) {
            OutputManager.getInstance().displayError("File receiving failed");
        }

    }
}
