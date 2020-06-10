/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.util;

import com.google.j2objc.annotations.AutoreleasePool;
import junit.framework.TestCase;

/**
 * iOS-specific unit tests for {@link HashMap}. The Android and Apache Harmony tests can't be reused
 * because they assume GC behavior. This class uses autorelease pools to detect memory leaks.
 *
 * @author Michał Pociecha-Łoś
 */
public class HashMapTest extends TestCase {
  public void testFinalize() {
    assertNoLeaks(
        () -> {
          HashMap<Counted, Counted> hashMap = new HashMap<>();
          for (int i = 0; i < 1000; i++) {
            Counted counted = new Counted();
            hashMap.put(counted, counted);
          }
        });
  }

  public void testClear() {
    HashMap<Counted, Counted> hashMap = new HashMap<>();
    assertNoLeaks(
        () -> {
          for (int i = 0; i < 1000; i++) {
            Counted counted = new Counted();
            hashMap.put(counted, counted);
          }
          hashMap.clear();
        });
  }

  public void testPutRemove() {
    HashMap<Counted, Counted> hashMap = new HashMap<>();
    assertNoLeaks(
        () -> {
          ArrayList<Counted> toRemove = new ArrayList<>();
          for (int i = 0; i < 1000; i++) {
            Counted counted = new Counted();
            hashMap.put(counted, counted);
            toRemove.add(counted);
          }
          for (Counted counted : toRemove) {
            hashMap.remove(counted);
          }
        });
  }

  int counter;

  final class Counted {
    {
      counter++;
    }

    protected void finalize() {
      counter--;
    }
  }

  @AutoreleasePool
  private void assertNoLeaks(Runnable runnable) {
    counter = 0;
    runInAutoreleasedPool(runnable);
    assertEquals("leak detected", 0, counter);
  }

  @AutoreleasePool
  private static void runInAutoreleasedPool(Runnable runnable) {
    runnable.run();
  }
}
