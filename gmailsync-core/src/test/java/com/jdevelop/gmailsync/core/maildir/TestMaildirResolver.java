package com.jdevelop.gmailsync.core.maildir;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.jdevelop.gmailsync.core.maildir.resolver.MaildirResolver;
import com.jdevelop.gmailsync.core.maildir.resolver.SimpleMaildirResolver;

import static junit.framework.Assert.assertNotNull;

public class TestMaildirResolver {

    private static final File basePath = new File("src/test/maildir-test");

    @Test
    public void testSimpleMaildirResolver() throws Exception {

        MaildirResolver resolver = new SimpleMaildirResolver();
        File[] resolvedMaildirs = resolver.resolveMaildirs(new File(
                "src/test/maildir-test"));
        assertNotNull(resolvedMaildirs);
        File[] expected = new File[] { createFile("maildir1"),
                createFile("maildir2"), createFile("container/maildir1"),
                createFile("container/maildir2") };
        Assert.assertArrayEquals(expected, resolvedMaildirs);
    }

    private File createFile(String path) {
        return new File(basePath, path);
    }

}
