package com.jdevelop.gmailsync.core.maildir.resolver;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Crawls the folders starting from the given one
 */
public class SimpleMaildirResolver implements MaildirResolver {

    private static Set<String> maildirSubFolders = new HashSet<String>(Arrays
            .asList("cur", "new", "tmp"));

    private static FileFilter possiblyMaildirFilter = new SkipMaildirSubfoldersFilter();

    @Override
    public File[] resolveMaildirs(File root) {
        List<File> maildirs = new ArrayList<File>();
        if (isMaildir(root))
            maildirs.add(root);
        crawlFilesystemFolders(root, maildirs);
        return maildirs.toArray(new File[maildirs.size()]);
    }

    private void crawlFilesystemFolders(File folder, List<File> maildirs) {
        File[] dirs = folder.listFiles(possiblyMaildirFilter);
        for (File dir : dirs) {
            if (isMaildir(dir))
                maildirs.add(dir);
            crawlFilesystemFolders(dir, maildirs);
        }
    }

    private boolean isMaildir(File folder) {
        boolean dirExists = false;
        for (String dir : maildirSubFolders) {
            if (new File(folder, dir).exists()) {
                dirExists = true;
                break;
            }
        }
        return dirExists;
    }

    private static class SkipMaildirSubfoldersFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return !maildirSubFolders.contains(pathname.getName());
        }

    }

}
