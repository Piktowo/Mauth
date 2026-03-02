package com.xinto.mauth.core.backup

import org.junit.Assert.assertEquals
import org.junit.Test

class WebDavClientTest {

    @Test
    fun `ensureFileUrl appends default filename to directory URL`() {
        val result = WebDavClient.ensureFileUrl("https://dav.jianguoyun.com/dav/")
        assertEquals("https://dav.jianguoyun.com/dav/Mauth/mauth_backup.txt", result)
    }

    @Test
    fun `ensureFileUrl appends default filename to directory URL without trailing slash`() {
        val result = WebDavClient.ensureFileUrl("https://dav.jianguoyun.com/dav")
        assertEquals("https://dav.jianguoyun.com/dav/Mauth/mauth_backup.txt", result)
    }

    @Test
    fun `ensureFileUrl preserves explicit file URL`() {
        val result = WebDavClient.ensureFileUrl("https://dav.example.com/path/mybackup.txt")
        assertEquals("https://dav.example.com/path/mybackup.txt", result)
    }

    @Test
    fun `ensureFileUrl handles URL with subdirectory`() {
        val result = WebDavClient.ensureFileUrl("https://dav.example.com/dav/subfolder/")
        assertEquals("https://dav.example.com/dav/subfolder/Mauth/mauth_backup.txt", result)
    }

    @Test
    fun `ensureFileUrl trims whitespace`() {
        val result = WebDavClient.ensureFileUrl("  https://dav.example.com/dav/  ")
        assertEquals("https://dav.example.com/dav/Mauth/mauth_backup.txt", result)
    }
}
