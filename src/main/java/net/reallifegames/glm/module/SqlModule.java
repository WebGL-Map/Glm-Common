/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Tyler Bucher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.reallifegames.glm.module;

import net.reallifegames.glm.api.GlmChunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Unifies sql related operations so all implementations are on the same page.
 *
 * @author Tyler Bucher
 */
public final class SqlModule {

    /**
     * The sql module version.
     */
    public static final int VERSION = 1;

    /**
     * The sql version constant.
     */
    @Nonnull
    public static final String SQL_VERSION_CONSTANT = "sql_version";

    /**
     * The sql create constants table query.
     */
    private static String CREATE_CONSTANTS_TABLE;

    /**
     * The sql create chunks table query.
     */
    private static String CREATE_CHUNKS_TABLE;

    /**
     * The sql create ban table query.
     */
    private static String CREATE_BAN_TABLE;

    /**
     * Sql insert or default sql constants query.
     */
    private static String INSERT_OR_DEFAULT_CONSTANTS;

    /**
     * Sql chunk exists query.
     */
    private static String CHUNK_EXISTS;

    /**
     * Sql chunk insert query.
     */
    private static String CHUNK_INSERT;

    /**
     * Sql chunk update query.
     */
    private static String CHUNK_UPDATE;

    /**
     * Sql get chunk query.
     */
    private static String GET_CHUNK;

    /**
     * Sql get chunks query.
     */
    private static String GET_CHUNKS;

    /**
     * Sql count total rows.
     */
    private static String COUNT_TOTAL_ROWS;

    /**
     * Sql count rows for world.
     */
    private static String COUNT_ROWS;

    /**
     * Sql delete rows for world.
     */
    private static String DELETE_ROWS;

    /**
     * Sql ban insert.
     */
    private static String INSERT_BAN;

    /**
     * Sql delete ban from ban table.
     */
    private static String DELETE_BAN;

    /**
     * Initializes this class and the query strings.
     *
     * @param databaseChunkPrefix the prefix for yhe chunks table.
     */
    public static void init(@Nonnull final String databaseChunkPrefix) {
        CREATE_CONSTANTS_TABLE = "CREATE TABLE IF NOT EXISTS `" + databaseChunkPrefix + "glm_constants` (`kkey` " +
                "VARCHAR(255) NOT NULL, `value` VARCHAR(255) NOT NULL, PRIMARY KEY (`kkey`)) ENGINE = InnoDB;";
        CREATE_CHUNKS_TABLE = "CREATE TABLE IF NOT EXISTS `" + databaseChunkPrefix + "glm_chunks` " +
                "(`world_id` CHAR(36) NOT NULL,`chunk_type` VARCHAR(32) NOT NULL,`position` POINT NOT NULL," +
                "`generation_time` BIGINT NOT NULL,`chunk_data` longtext NOT NULL,`height_data` longtext NOT NULL," +
                "`biome_data` longtext NOT NULL,`index_data` longtext NOT NULL,INDEX `world_id` (`world_id`)," +
                "INDEX `position` (`position`),INDEX `chunk_type` (`chunk_type`)) ENGINE = InnoDB;";
        CREATE_BAN_TABLE = "CREATE TABLE IF NOT EXISTS `" + databaseChunkPrefix + "glm_bans` " +
                "(`ip_address` VARCHAR(45) NOT NULL , `client_id` CHAR(36) NOT NULL , INDEX " +
                "(`ip_address`), INDEX (`client_id`)) ENGINE = InnoDB;";
        CHUNK_EXISTS = "SELECT EXISTS(SELECT 1 FROM `" + databaseChunkPrefix + "glm_chunks` WHERE `world_id` = ? AND " +
                "`chunk_type`=? AND `position` = POINT(?, ?));";
        CHUNK_INSERT = "INSERT INTO `" + databaseChunkPrefix + "glm_chunks` (`world_id`, `chunk_type`, `position`, " +
                "`generation_time`, `chunk_data`, `height_data`, `biome_data`, `index_data`) VALUES " +
                "(?, ?, POINT(?, ?), ?, ?, ?, ?, ?)";
        CHUNK_UPDATE = "UPDATE `" + databaseChunkPrefix + "glm_chunks` SET `generation_time`=?,`chunk_data`=?," +
                "`height_data`=?,`biome_data`=?,`index_data`=? WHERE `world_id` = ? AND `chunk_type` = ? AND `position` = POINT(?, ?);";
        GET_CHUNK = "SELECT `generation_time`, `chunk_data`, `height_data`, `biome_data`, `index_data` FROM `" + databaseChunkPrefix +
                "glm_chunks` WHERE `world_id`=? AND `chunk_type` = ? AND `position`=POINT(?,?);";
        GET_CHUNKS = "SELECT `generation_time`, ST_X(`position`) as X, ST_Y(`position`) as Z, `chunk_data`, `height_data`" +
                ", `biome_data`, `index_data` FROM `" + databaseChunkPrefix + "glm_chunks` WHERE `world_id`=? AND " +
                "`chunk_type` = ? AND `position` IN ";
        COUNT_TOTAL_ROWS = "SELECT COUNT(*) FROM `" + databaseChunkPrefix + "glm_chunks`";
        COUNT_ROWS = "SELECT COUNT(*) FROM `" + databaseChunkPrefix + "glm_chunks` WHERE `world_id` = ?;";
        DELETE_ROWS = "DELETE FROM `" + databaseChunkPrefix + "glm_chunks` WHERE `world_id`=? AND `position` IN ";
        INSERT_BAN = "INSERT INTO `" + databaseChunkPrefix + "glm_bans`(`ip_address`, `client_id`) VALUES (?, ?)";
        DELETE_BAN = "DELETE FROM `" + databaseChunkPrefix + "glm_bans` WHERE ";
    }

    /**
     * @return the sql create chunks table query.
     */
    public static String getCreateChunksTableSqlString() {
        return CREATE_CHUNKS_TABLE;
    }

    /**
     * @return the sql create ban table query.
     */
    public static String getCreateBanTableSqlString() {
        return CREATE_BAN_TABLE;
    }

    /**
     * @return the sql chunk exists query.
     */
    public static String getChunkExistsSqlString() {
        return CHUNK_EXISTS;
    }

    /**
     * @return the sql chunk insert query.
     */
    public static String getChunkInsertSqlString() {
        return CHUNK_INSERT;
    }

    /**
     * @return the sql chunk update query.
     */
    public static String getChunkUpdateSqlString() {
        return CHUNK_UPDATE;
    }

    /**
     * @return the sql get chunk query.
     */
    public static String getGetChunkSqlString() {
        return GET_CHUNK;
    }

    /**
     * @return the sql get chunks query.
     */
    public static String getPartialGetChunksSqlString() {
        return GET_CHUNKS;
    }

    /**
     * @return the sql count total rows.
     */
    public static String getCountTotalRowsSqlString() {
        return COUNT_TOTAL_ROWS;
    }

    /**
     * @return the sql count rows for world.
     */
    public static String getCountRowsSqlString() {
        return COUNT_ROWS;
    }

    /**
     * @return the sql delete rows for world.
     */
    public static String getDeleteRowsSqlString() {
        return DELETE_ROWS;
    }

    /**
     * Creates the glm constants sql table.
     *
     * @param connection the sql database connection.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void createConstantsTable(@Nonnull final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CONSTANTS_TABLE);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * Creates the chunk sql table.
     *
     * @param connection the sql database connection.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void createChunksTable(@Nonnull final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CHUNKS_TABLE);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * Creates the ban sql table.
     *
     * @param connection the sql database connection.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void createBansTable(@Nonnull final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CHUNKS_TABLE);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * Attempts to update the sql version constant.
     *
     * @param connection the sql database connection.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void setSqlVersion(@Nonnull final Connection connection) throws SQLException {
        final PreparedStatement updateStatement = connection.prepareStatement(INSERT_OR_DEFAULT_CONSTANTS);
        updateStatement.setString(1, SQL_VERSION_CONSTANT);
        updateStatement.setString(2, String.valueOf(VERSION));
        updateStatement.executeUpdate();
        updateStatement.close();
    }

    /**
     * Checks to see if a row exists.
     *
     * @param connection the sql database connection.
     * @param worldId    the id of the world.
     * @param chunkType  the glm chunk type.
     * @param x          the x position of the chunk.
     * @param z          the z position of the chunk.
     * @return true if the row exists false otherwise.
     *
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static boolean rowExists(@Nonnull final Connection connection, @Nonnull final String worldId,
                                    @Nonnull final String chunkType, int x, final int z) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(CHUNK_EXISTS);
        // Set parameters
        preparedStatement.setString(1, worldId);
        preparedStatement.setString(2, chunkType);
        preparedStatement.setInt(3, x);
        preparedStatement.setInt(4, z);
        // Execute query
        final ResultSet results = preparedStatement.executeQuery();
        final boolean returnVal = results.next() && results.getBoolean(1);
        results.close();
        preparedStatement.close();
        return returnVal;
    }

    /**
     * Attempts to update a chunk in the sql database.
     *
     * @param connection the sql database connection.
     * @param worldId    the id of the world.
     * @param chunkType  the glm chunk type.
     * @param x          the x position of the chunk.
     * @param z          the z position of the chunk.
     * @param glChunk    the data to update the sql row with.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void updateGlChunk(@Nonnull final Connection connection, @Nonnull final String worldId,
                                     @Nonnull final String chunkType, final int x, final int z,
                                     @Nonnull final GlmChunk glChunk) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(CHUNK_EXISTS);
        // Set parameters
        preparedStatement.setString(1, worldId);
        preparedStatement.setString(2, chunkType);
        preparedStatement.setInt(3, x);
        preparedStatement.setInt(4, z);
        // Execute query
        ResultSet results = preparedStatement.executeQuery();
        if (results.next()) {
            if (results.getBoolean(1)) {
                preparedStatement = connection.prepareStatement(CHUNK_UPDATE);
                // Set parameters
                preparedStatement.setLong(1, glChunk.getChunkGenerationTime());
                preparedStatement.setString(2, glChunk.getBlockData());
                preparedStatement.setString(3, glChunk.getBlockHeightData());
                preparedStatement.setString(4, glChunk.getBlockBiomeData());
                preparedStatement.setString(5, glChunk.getBlockIndices());
                preparedStatement.setString(6, worldId);
                preparedStatement.setString(7, chunkType);
                preparedStatement.setInt(8, x);
                preparedStatement.setInt(9, z);
                // Execute query
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement(CHUNK_INSERT);
                // Set parameters
                preparedStatement.setString(1, worldId);
                preparedStatement.setString(2, chunkType);
                preparedStatement.setInt(3, x);
                preparedStatement.setInt(4, z);
                preparedStatement.setLong(5, glChunk.getChunkGenerationTime());
                preparedStatement.setString(6, glChunk.getBlockData());
                preparedStatement.setString(7, glChunk.getBlockHeightData());
                preparedStatement.setString(8, glChunk.getBlockBiomeData());
                preparedStatement.setString(9, glChunk.getBlockIndices());
                // Execute query
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Counts the total rows for the main sql table.
     *
     * @param connection the sql database connection.
     * @return the number of rows in the table.
     *
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static int countTotalRows(@Nonnull final Connection connection) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(COUNT_TOTAL_ROWS);
        // Execute query
        final ResultSet results = preparedStatement.executeQuery();
        final int returnVal = results.next() ? results.getInt(1) : 0;
        results.close();
        preparedStatement.close();
        return returnVal;
    }

    /**
     * Counts the total rows for the main sql table filtered by the world id.
     *
     * @param connection the sql database connection.
     * @param worldId    the if of the world to count rows for.
     * @return the number of rows in the table for a world id.
     *
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static int countRowsForWorld(@Nonnull final Connection connection, @Nonnull final String worldId)
            throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(COUNT_ROWS);
        preparedStatement.setString(1, worldId);
        // Execute query
        final ResultSet results = preparedStatement.executeQuery();
        final int returnVal = results.next() ? results.getInt(1) : 0;
        results.close();
        preparedStatement.close();
        return returnVal;
    }

    /**
     * @param positions the list of points to get chunks for. x, z interleaved
     * @return the newly built sql get chunk query.
     */
    @Nonnull
    public static String getNewGetChunks(@Nonnull final List<Integer> positions) {
        final StringBuilder builder = new StringBuilder(GET_CHUNKS).append("(");
        int i = 0;
        while (i < positions.size()) {
            builder.append("POINT(").append(positions.get(i++)).append(',').append(positions.get(i++)).append("),");
        }
        // Remove extra character
        builder.deleteCharAt(builder.length() - 1).append(");");
        return builder.toString();
    }

    /**
     * Attempts to remove chunks from the sql server.
     *
     * @param connection the sql database connection.
     * @param worldId    the id of the the world to check.
     * @param x1         the top left x coordinate.
     * @param z1         the top left z coordinate.
     * @param x2         the bottom right x coordinate.
     * @param z2         the bottom right z coordinate.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void removeChunks(@Nonnull final Connection connection, @Nonnull final String worldId, int x1, int z1,
                                    int x2, int z2) throws SQLException {
        final StringBuilder builder = new StringBuilder(DELETE_ROWS).append("(");
        for (int i = x1; i < x2; i++) {
            for (int j = z1; j < z2; j++) {
                builder.append("POINT(").append(i).append(',').append(j).append("),");
            }
        }
        final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ROWS);
        preparedStatement.setString(1, worldId);
        // Execute query
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * Inserts a ban into the ban table.
     *
     * @param connection the sql database connection.
     * @param ipAddress  the ip address of the client.
     * @param uuid       the uuid of the client map.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void insertBan(@Nonnull final Connection connection, @Nonnull final String ipAddress,
                                 @Nonnull final String uuid) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BAN);
        preparedStatement.setString(1, ipAddress);
        preparedStatement.setString(2, uuid);
        // Execute update
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * Removes a ban from the ban table.
     *
     * @param connection the sql database connection.
     * @param ipAddress  the ip address of the client.
     * @param uuid       the uuid of the client map.
     * @throws SQLException if a database access error occurs; this method is called on a closed PreparedStatement or
     *                      the SQL statement returns a ResultSet object.
     */
    public static void removeBan(@Nonnull final Connection connection, @Nullable final String ipAddress,
                                 @Nullable final String uuid) throws SQLException {
        if (ipAddress != null || uuid != null) {
            StringBuilder NEW_DELETE_BAN = new StringBuilder(DELETE_BAN);
            boolean isIp = false;
            if (ipAddress != null) {
                NEW_DELETE_BAN.append("`ip_address` = ?");
                isIp = true;
            }
            if (isIp) {
                NEW_DELETE_BAN.append("AND ");
            }
            if (uuid != null) {
                NEW_DELETE_BAN.append("`client_id` = ?;");
            }
            final PreparedStatement preparedStatement = connection.prepareStatement(NEW_DELETE_BAN.toString());
            if (isIp) {
                preparedStatement.setString(1, ipAddress);
            }
            preparedStatement.setString(isIp ? 2 : 1, uuid);
            // Execute update
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }
}
