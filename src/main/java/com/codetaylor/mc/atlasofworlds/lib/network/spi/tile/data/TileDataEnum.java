package com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.data;

import com.codetaylor.mc.atlasofworlds.lib.network.spi.tile.TileDataBase;
import net.minecraft.network.FriendlyByteBuf;

public class TileDataEnum<E extends Enum>
    extends TileDataBase {

  public interface EnumReader<E extends Enum> {

    E read(int ordinal);

  }

  public interface EnumWriter<E extends Enum> {

    int write(E value);

  }

  private final EnumReader<E> reader;
  private final EnumWriter<E> writer;
  private E value;

  public TileDataEnum(EnumReader<E> reader, EnumWriter<E> writer, E initialValue) {

    this(reader, writer, initialValue, 1);
  }

  public TileDataEnum(EnumReader<E> reader, EnumWriter<E> writer, E initialValue, int updateInterval) {

    super(updateInterval);
    this.reader = reader;
    this.writer = writer;
    this.set(initialValue);
  }

  public void set(E value) {

    if (value != this.value) {
      this.value = value;
      this.setDirty(true);
    }
  }

  public E get() {

    return this.value;
  }

  @Override
  public void read(FriendlyByteBuf buffer) {

    this.value = this.reader.read(buffer.readInt());
  }

  @Override
  public void write(FriendlyByteBuf buffer) {

    buffer.writeInt(this.writer.write(this.value));
  }
}
