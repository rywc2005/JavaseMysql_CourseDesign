package com.PFM.CD.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件操作工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class FileUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private FileUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 读取文本文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException 如果读取过程中发生IO错误
     */
    public static String readTextFile(String filePath) throws IOException {
        return readTextFile(filePath, StandardCharsets.UTF_8);
    }

    /**
     * 读取文本文件内容（指定字符集）
     *
     * @param filePath 文件路径
     * @param charset 字符集
     * @return 文件内容
     * @throws IOException 如果读取过程中发生IO错误
     */
    public static String readTextFile(String filePath, Charset charset) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }

        return content.toString();
    }

    /**
     * 读取文本文件内容为行列表
     *
     * @param filePath 文件路径
     * @return 行列表
     * @throws IOException 如果读取过程中发生IO错误
     */
    public static List<String> readLines(String filePath) throws IOException {
        return readLines(filePath, StandardCharsets.UTF_8);
    }

    /**
     * 读取文本文件内容为行列表（指定字符集）
     *
     * @param filePath 文件路径
     * @param charset 字符集
     * @return 行列表
     * @throws IOException 如果读取过程中发生IO错误
     */
    public static List<String> readLines(String filePath, Charset charset) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    /**
     * 写入文本到文件
     *
     * @param filePath 文件路径
     * @param content 文件内容
     * @throws IOException 如果写入过程中发生IO错误
     */
    public static void writeTextFile(String filePath, String content) throws IOException {
        writeTextFile(filePath, content, StandardCharsets.UTF_8);
    }

    /**
     * 写入文本到文件（指定字符集）
     *
     * @param filePath 文件路径
     * @param content 文件内容
     * @param charset 字符集
     * @throws IOException 如果写入过程中发生IO错误
     */
    public static void writeTextFile(String filePath, String content, Charset charset) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), charset))) {
            writer.write(content);
        }
    }

    /**
     * 写入行列表到文件
     *
     * @param filePath 文件路径
     * @param lines 行列表
     * @throws IOException 如果写入过程中发生IO错误
     */
    public static void writeLines(String filePath, List<String> lines) throws IOException {
        writeLines(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * 写入行列表到文件（指定字符集）
     *
     * @param filePath 文件路径
     * @param lines 行列表
     * @param charset 字符集
     * @throws IOException 如果写入过程中发生IO错误
     */
    public static void writeLines(String filePath, List<String> lines, Charset charset) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), charset))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException 如果复制过程中发生IO错误
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);

        // 确保目标文件的父目录存在
        Files.createDirectories(target.getParent());

        // 复制文件，如果目标文件存在则替换
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 复制目录
     *
     * @param sourceDir 源目录路径
     * @param targetDir 目标目录路径
     * @throws IOException 如果复制过程中发生IO错误
     */
    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        Path source = Paths.get(sourceDir);
        Path target = Paths.get(targetDir);

        // 确保源目录存在
        if (!Files.exists(source) || !Files.isDirectory(source)) {
            throw new IOException("源目录不存在或不是一个目录: " + sourceDir);
        }

        // 确保目标目录存在
        Files.createDirectories(target);

        // 复制目录内容
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path relativePath = source.relativize(sourcePath);
                Path targetPath = target.resolve(relativePath);

                if (Files.isDirectory(sourcePath)) {
                    if (!Files.exists(targetPath)) {
                        Files.createDirectory(targetPath);
                    }
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 如果成功删除返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * 删除目录及其内容
     *
     * @param dirPath 目录路径
     * @return 如果成功删除返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirPath) {
        File dir = new File(dirPath);
        return deleteDirectory(dir);
    }

    /**
     * 删除目录及其内容
     *
     * @param dir 目录
     * @return 如果成功删除返回true，否则返回false
     */
    private static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    boolean success = deleteDirectory(file);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录路径
     * @return 如果成功创建或已存在返回true，否则返回false
     */
    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return dir.isDirectory();
        }
        return dir.mkdirs();
    }

    /**
     * 获取文件扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名（不包含点号）
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        int dotIndex = filePath.lastIndexOf(".");
        if (dotIndex < 0 || dotIndex == filePath.length() - 1) {
            return "";
        }

        return filePath.substring(dotIndex + 1);
    }

    /**
     * 获取文件名（不包含路径和扩展名）
     *
     * @param filePath 文件路径
     * @return 文件名（不包含路径和扩展名）
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        // 移除路径
        String fileName = new File(filePath).getName();

        // 移除扩展名
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return fileName;
        }

        return fileName.substring(0, dotIndex);
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return 如果文件存在返回true，否则返回false
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 检查目录是否存在
     *
     * @param dirPath 目录路径
     * @return 如果目录存在返回true，否则返回false
     */
    public static boolean directoryExists(String dirPath) {
        File dir = new File(dirPath);
        return dir.exists() && dir.isDirectory();
    }

    /**
     * 获取文件大小（字节）
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     * @throws IOException 如果获取文件大小过程中发生IO错误
     */
    public static long getFileSize(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在或不是一个文件: " + filePath);
        }

        return file.length();
    }

    /**
     * 压缩文件或目录到ZIP文件
     *
     * @param sourcePath 源文件或目录路径
     * @param zipFilePath ZIP文件路径
     * @throws IOException 如果压缩过程中发生IO错误
     */
    public static void zipFile(String sourcePath, String zipFilePath) throws IOException {
        File sourceFile = new File(sourcePath);

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            if (sourceFile.isFile()) {
                // 压缩单个文件
                addFileToZip(zos, sourceFile, sourceFile.getName());
            } else {
                // 压缩目录
                addDirectoryToZip(zos, sourceFile, "");
            }
        }
    }

    /**
     * 将文件添加到ZIP输出流
     *
     * @param zos ZIP输出流
     * @param file 文件
     * @param entryName ZIP条目名称
     * @throws IOException 如果添加过程中发生IO错误
     */
    private static void addFileToZip(ZipOutputStream zos, File file, String entryName) throws IOException {
        byte[] buffer = new byte[4096];

        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

    /**
     * 将目录添加到ZIP输出流
     *
     * @param zos ZIP输出流
     * @param dir 目录
     * @param path 目录在ZIP文件中的路径
     * @throws IOException 如果添加过程中发生IO错误
     */
    private static void addDirectoryToZip(ZipOutputStream zos, File dir, String path) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String entryName = path.isEmpty() ? file.getName() : path + "/" + file.getName();

            if (file.isDirectory()) {
                // 添加目录（空目录）
                ZipEntry zipEntry = new ZipEntry(entryName + "/");
                zos.putNextEntry(zipEntry);
                zos.closeEntry();

                // 递归添加子目录
                addDirectoryToZip(zos, file, entryName);
            } else {
                // 添加文件
                addFileToZip(zos, file, entryName);
            }
        }
    }

    /**
     * 解压ZIP文件
     *
     * @param zipFilePath ZIP文件路径
     * @param destDir 目标目录路径
     * @throws IOException 如果解压过程中发生IO错误
     */
    public static void unzipFile(String zipFilePath, String destDir) throws IOException {
        File destDirectory = new File(destDir);
        if (!destDirectory.exists()) {
            destDirectory.mkdirs();
        }

        byte[] buffer = new byte[4096];

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());

                // 创建父目录
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // 确保父目录存在
                    File parent = newFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    // 提取文件
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }

                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }
    }

    /**
     * 获取文件的MIME类型
     *
     * @param filePath 文件路径
     * @return MIME类型
     * @throws IOException 如果获取过程中发生IO错误
     */
    public static String getMimeType(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.probeContentType(path);
    }
}