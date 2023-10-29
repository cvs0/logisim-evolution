/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.draw.util;

import com.cburch.draw.model.CanvasModel;
import com.cburch.draw.model.CanvasObject;

import java.util.*;

public final class ZOrder {
  private ZOrder() {
    // dummy
  }

  // Find the index of a CanvasObject within a List of CanvasObjects.
  private static int getIndex(CanvasObject query, List<CanvasObject> objs) {
    // Check that query and objs are not null
    Objects.requireNonNull(query, "query must not be null");
    Objects.requireNonNull(objs, "objs must not be null");

    // Iterate through the list of objects to find the index of the query object
    for (int index = 0; index < objs.size(); index++) {
      if (query.equals(objs.get(index))) {
        return index;
      }
    }

    // If the query object is not found, return -1
    return -1;
  }

  // returns first object above query in the z-order that overlaps query
  public static CanvasObject getObjectAbove(CanvasObject query, CanvasModel model, Collection<? extends CanvasObject> ignore) {
    // Check that query, model, and ignore are not null
    Objects.requireNonNull(query, "query must not be null");
    Objects.requireNonNull(model, "model must not be null");
    Objects.requireNonNull(ignore, "ignore must not be null");

    // Call the getPrevious method to find the object above the query object
    return getPrevious(query, model.getObjectsFromTop(), model, ignore);
  }

  // returns first object below query in the z-order that overlaps query
  public static CanvasObject getObjectBelow(CanvasObject query, CanvasModel model, Collection<? extends CanvasObject> ignore) {
    // Check that query, model, and ignore are not null
    Objects.requireNonNull(query, "query must not be null");
    Objects.requireNonNull(model, "model must not be null");
    Objects.requireNonNull(ignore, "ignore must not be null");

    // Call the getPrevious method to find the object below the query object
    return getPrevious(query, model.getObjectsFromBottom(), model, ignore);
  }

  // Find the CanvasObject directly above the given query object within a list of CanvasObjects, considering a collection of objects to ignore.
  private static CanvasObject getPrevious(
          CanvasObject query,
          List<CanvasObject> objs,
          CanvasModel model,
          Collection<? extends CanvasObject> ignore) {
    // Check that query, objs, model, and ignore are not null
    Objects.requireNonNull(query, "query must not be null");
    Objects.requireNonNull(objs, "objs must not be null");
    Objects.requireNonNull(model, "model must not be null");
    Objects.requireNonNull(ignore, "ignore must not be null");

    // Find the index of the query object in the list
    var index = getIndex(query, objs);

    // If the query object is found and not at the bottom, search for the object above it
    if (index > 0) {
      final var set = toSet(model.getObjectsOverlapping(query));
      final var it = objs.listIterator(index);

      // Iterate in reverse order to find the object above the query object
      while (it.hasPrevious()) {
        final var o = it.previous();
        if (set.contains(o) && !ignore.contains(o)) return o;
      }
    }

    // If no object is found above, return null
    return null;
  }

  // Get the Z-index of a CanvasObject in a CanvasModel, representing its position from the bottom.
  public static int getZIndex(CanvasObject query, CanvasModel model) {
    // Check that model is not null
    Objects.requireNonNull(model, "model must not be null");

    // Check that query is not null
    if (query == null) {
      throw new IllegalArgumentException("query must not be null");
    }

    // Call the getIndex method to find the Z-index of the query object
    return getIndex(query, model.getObjectsFromBottom());
  }

  public static Map<CanvasObject, Integer> getZIndex(
          Collection<? extends CanvasObject> query, CanvasModel model) {
    Objects.requireNonNull(model, "model must not be null");

    if (query == null) {
      return Map.of();
    }

    final var querySet = toSet(query);
    final var ret = new LinkedHashMap<CanvasObject, Integer>(query.size());
    var z = -1;
    for (final var o : model.getObjectsFromBottom()) {
      z++;
      if (querySet.contains(o)) {
        ret.put(o, z);
      }
    }
    return ret;
  }


  public static <E extends CanvasObject> List<E> sortBottomFirst(
          Collection<E> objects, CanvasModel model) {
    Objects.requireNonNull(objects, "objects must not be null");
    Objects.requireNonNull(model, "model must not be null");

    return sortXFirst(objects, model, model.getObjectsFromTop());
  }

  public static <E extends CanvasObject> List<E> sortTopFirst(
          Collection<E> objects, CanvasModel model) {
    Objects.requireNonNull(objects, "objects must not be null");
    Objects.requireNonNull(model, "model must not be null");

    return sortXFirst(objects, model, model.getObjectsFromBottom());
  }

  private static <E extends CanvasObject> List<E> sortXFirst(
          Collection<E> objects, CanvasModel model, Collection<CanvasObject> objs) {
    Objects.requireNonNull(objects, "objects must not be null");
    Objects.requireNonNull(model, "model must not be null");
    Objects.requireNonNull(objs, "objs must not be null");

    Set<E> set = toSet(objects);
    List<E> ret = new ArrayList<>(objects.size());
    
    for (final var o : objs) {
      if (set.contains(o)) {
        @SuppressWarnings("unchecked")
        E toAdd = (E) o;
        ret.add(toAdd);
      }
    }
    return ret;
  }

  private static <E> Set<E> toSet(Collection<E> objects) {
    Objects.requireNonNull(objects, "objects must not be null");
    return (objects instanceof Set) ? (Set<E>) objects : new HashSet<>(objects);
  }
}
