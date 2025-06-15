package com.technokratos.eateasy.cart.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class QueryProvider {

    private static QueryProvider instance;

    private final Map<String, String> sqlQueriesForCart;

    public static QueryProvider getInstance() {
        if (instance == null) {
            instance = new QueryProvider();
        }
        return instance;
    }

    private QueryProvider() {
        this.sqlQueriesForCart = new HashMap<>();
        loadAllSqlQueries("query/cart", sqlQueriesForCart);
    }

    public void loadAllSqlQueries(String directory, Map<String, String> sqlQueriesMap) {
        try {
            Path dirPath =
                    Paths.get(
                            Objects.requireNonNull(getClass().getClassLoader().getResource(directory)).toURI());

            Files.walk(dirPath)
                    .filter(Files::isRegularFile)
                    .forEach(
                            path -> {
                                if (path.toString().endsWith(".sql")) {
                                    String fileName = path.getFileName().toString().replace(".sql", "");
                                    String sqlQuery = readSqlFile(path);
                                    sqlQueriesMap.put(fileName, sqlQuery);
                                }
                            });
        } catch (IOException e) {
            throw new RuntimeException("Error loading SQL queries from directory: %s".formatted(directory), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String readSqlFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Error reading SQL file: %s".formatted(path), e);
        }
    }

    public String getSqlQueryForCart(String queryName) {
        return sqlQueriesForCart.get(queryName);
    }
}