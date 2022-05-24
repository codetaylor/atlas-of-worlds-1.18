/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2022 Joseph Bettendorff a.k.a. "Commoble"
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.codetaylor.mc.atlasofworlds.lib.dimension.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires when a dimension/level is about to be unregistered by Infiniverse.<br>
 * This event fires on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} and is not cancellable.<br>
 */
public class UnregisterDimensionEvent
    extends Event {

  private final ServerLevel level;

  public UnregisterDimensionEvent(ServerLevel level) {

    this.level = level;
  }

  /**
   * @return The level that is about to be unregistered by Infiniverse.
   */
  public ServerLevel getLevel() {

    return this.level;
  }
}
