/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tile;

public class Chunk {
    private final int chunkX, chunkY; // Chunk position in the map grid
    private final int[][] mapTileNum; // 2D array storing tile indices for this chunk
    private final int size;// Size of the chunk (number of tiles per side)

    public Chunk(int chunkX, int chunkY, int size){
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.size = size;
        mapTileNum = new int[size][size];// initialize tile indices
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public int getSize() {
        return size;
    }

    public int getTileNum(int row, int col) {
        return mapTileNum[row][col];
    }

    public void setTileNum(int row, int col, int tileNum) {
        mapTileNum[row][col] = tileNum;
    }
}
