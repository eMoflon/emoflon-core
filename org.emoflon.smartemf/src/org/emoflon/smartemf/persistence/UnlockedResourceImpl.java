package org.emoflon.smartemf.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.common.notify.impl.NotifyingListImpl;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.SegmentSequence;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.EcoreUtil.ContentTreeIterator;
import org.eclipse.emf.ecore.util.EcoreUtil.ProperContentIterator;

public abstract class UnlockedResourceImpl extends NotifierImpl implements Resource, Resource.Internal {
	/**
	 * The default URI converter when there is no resource set.
	 */
	private static URIConverter defaultURIConverter;

	/**
	 * Returns the default URI converter that's used when there is no resource set.
	 * 
	 * @return the default URI converter.
	 * @see #getURIConverter
	 */
	protected static URIConverter getDefaultURIConverter() {
		if (defaultURIConverter == null) {
			defaultURIConverter = new ExtensibleURIConverterImpl();
		}
		return defaultURIConverter;
	}

	/**
	 * The storage for the default save options.
	 */
	protected Map<Object, Object> defaultSaveOptions;

	/**
	 * The storage for the default load options.
	 */
	protected Map<Object, Object> defaultLoadOptions;

	/**
	 * The storage for the default delete options.
	 */
	protected Map<Object, Object> defaultDeleteOptions;

	/**
	 * The containing resource set.
	 * 
	 * @see #getResourceSet
	 */
	protected ResourceSet resourceSet;

	/**
	 * The URI.
	 * 
	 * @see #getURI
	 */
	protected URI uri;

	/**
	 * The time stamp.
	 * 
	 * @see #getTimeStamp
	 */
	protected long timeStamp;

	/**
	 * The errors.
	 * 
	 * @see #getErrors
	 */
	protected EList<Diagnostic> errors;

	/**
	 * The warnings.
	 * 
	 * @see #getErrors
	 */
	protected EList<Diagnostic> warnings;

	/**
	 * The modified flag.
	 * 
	 * @see #isModified
	 */
	protected boolean isModified;

	/**
	 * The loaded flag.
	 * 
	 * @see #isLoaded
	 */
	protected boolean isLoaded;

	/**
	 * The loading flag.
	 * 
	 * @see #isLoading
	 */
	protected boolean isLoading;

	/**
	 * A copy of the {@link #contents contents} list while the contents are being
	 * {@link #unload() unloaded}. I.e., if this is not <code>null</code>, then the
	 * resource is in the process of unloading.
	 * 
	 * @see #unload()
	 */
	protected List<EObject> unloadingContents;

	/**
	 * The modification tracking adapter.
	 * 
	 * @see #isTrackingModification
	 * @see #attached(EObject)
	 * @see #detached(EObject)
	 */
	protected Adapter modificationTrackingAdapter;

	/**
	 * A map to retrieve the EObject based on the value of its ID feature.
	 * 
	 * @see #setIntrinsicIDToEObjectMap(Map)
	 */
	protected Map<String, EObject> intrinsicIDToEObjectMap;

	/**
	 * Creates a empty instance.
	 */
	public UnlockedResourceImpl() {
		super();
	}

	/**
	 * Creates an instance with the given URI.
	 * 
	 * @param uri the URI.
	 */
	public UnlockedResourceImpl(URI uri) {
		this();
		this.uri = uri;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	/**
	 * Sets the new containing resource set, and removes the resource from a
	 * previous containing resource set, if necessary.
	 * 
	 * @param resourceSet   the new containing resource set.
	 * @param notifications the accumulating notifications.
	 * @return notification of the change.
	 */
	public NotificationChain basicSetResourceSet(ResourceSet resourceSet, NotificationChain notifications) {
		ResourceSet oldResourceSet = this.resourceSet;
		if (oldResourceSet != null) {
			notifications = ((InternalEList<Resource>) oldResourceSet.getResources()).basicRemove(this, notifications);
		}

		this.resourceSet = resourceSet;

		if (eNotificationRequired()) {
			if (notifications == null) {
				notifications = new NotificationChainImpl(2);
			}
			notifications.add(new NotificationImpl(Notification.SET, oldResourceSet, resourceSet) {
				@Override
				public Object getNotifier() {
					return null;
				}

				@Override
				public int getFeatureID(Class<?> expectedClass) {
					return RESOURCE__RESOURCE_SET;
				}
			});
		}

		return notifications;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public URI getURI() {
		return uri;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public void setURI(URI uri) {
		URI oldURI = this.uri;
		this.uri = uri;
		if (eNotificationRequired()) {
			Notification notification = new NotificationImpl(Notification.SET, oldURI, uri) {
				@Override
				public Object getNotifier() {
					return null;
				}

				@Override
				public int getFeatureID(Class<?> expectedClass) {
					return RESOURCE__URI;
				}
			};
			eNotify(notification);
		}
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		long oldTimeStamp = this.timeStamp;
		this.timeStamp = timeStamp;
		if (eNotificationRequired()) {
			Notification notification = new NotificationImpl(Notification.SET, oldTimeStamp, timeStamp) {
				@Override
				public Object getNotifier() {
					return null;
				}

				@Override
				public int getFeatureID(Class<?> expectedClass) {
					return RESOURCE__TIME_STAMP;
				}
			};
			eNotify(notification);
		}
	}

	/*
	 * Javadoc copied from interface.
	 */
	public TreeIterator<EObject> getAllContents() {
		return new AbstractTreeIterator<EObject>(this, false) {
			private static final long serialVersionUID = 1L;

			@Override
			public Iterator<EObject> getChildren(Object object) {
				return object == UnlockedResourceImpl.this ? UnlockedResourceImpl.this.getContents().iterator()
						: ((EObject) object).eContents().iterator();
			}
		};
	}

	protected TreeIterator<EObject> getAllProperContents(EObject eObject) {
		return EcoreUtil.getAllProperContents(eObject, false);
	}

	protected TreeIterator<EObject> getAllProperContents(List<EObject> contents) {
		return new ContentTreeIterator<EObject>(contents, false) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<EObject> getChildren(Object object) {
				return object == this.object ? ((List<EObject>) object).iterator()
						: new ProperContentIterator<EObject>(((EObject) object));
			}
		};
	}

	/*
	 * Javadoc copied from interface.
	 */
	public EList<Diagnostic> getErrors() {
		if (errors == null) {
			errors = new NotifyingListImpl<Diagnostic>() {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean isNotificationRequired() {
					return UnlockedResourceImpl.this.eNotificationRequired();
				}

				@Override
				public Object getNotifier() {
					return UnlockedResourceImpl.this;
				}

				@Override
				public int getFeatureID() {
					return RESOURCE__ERRORS;
				}
			};
		}
		return errors;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public EList<Diagnostic> getWarnings() {
		if (warnings == null) {
			warnings = new NotifyingListImpl<Diagnostic>() {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean isNotificationRequired() {
					return eNotificationRequired();
				}

				@Override
				public Object getNotifier() {
					return null;
				}

				@Override
				public int getFeatureID() {
					return RESOURCE__WARNINGS;
				}
			};
		}
		return warnings;
	}

	/**
	 * Returns whether contents will be compressed. This implementation returns
	 * <code>false</code>. When this returns <code>true</code>,
	 * {@link #save(OutputStream, Map)} and {@link #load(InputStream, Map)} will zip
	 * compress and decompress contents.
	 * 
	 * @return whether contents will be compressed.
	 * @see #newContentZipEntry
	 * @see #isContentZipEntry(ZipEntry)
	 */
	public boolean useZip() {
		return false;
	}

	/**
	 * Returns the URI fragment root segment for reaching the given direct content
	 * object. This default implementation returns the position of the object, if
	 * there is more than one object, otherwise, the empty string. As a result, the
	 * URI fragment for a single root object will be <code>"/"</code>.
	 * 
	 * @return the URI fragment root segment for reaching the given direct content
	 *         object.
	 */
	protected String getURIFragmentRootSegment(EObject eObject) {
		List<EObject> contents = unloadingContents != null ? unloadingContents : getContents();
		return contents.size() > 1 ? Integer.toString(contents.indexOf(eObject)) : "";
	}

	/*
	 * Javadoc copied from interface.
	 */
	public String getURIFragment(EObject eObject) {
		String id = EcoreUtil.getID(eObject);
		if (id != null) {
			return id;
		} else {
			InternalEObject internalEObject = (InternalEObject) eObject;
			if (internalEObject.eDirectResource() == this
					|| unloadingContents != null && unloadingContents.contains(internalEObject)) {
				return "/" + getURIFragmentRootSegment(eObject);
			} else {
				SegmentSequence.Builder builder = SegmentSequence.newBuilder("/");

				boolean supportIDRelativeURIFragmentPaths = supportIDRelativeURIFragmentPaths();
				boolean isContained = false;
				for (InternalEObject container = internalEObject
						.eInternalContainer(); container != null; container = internalEObject.eInternalContainer()) {
					// If we've not already found an ID, which will be the last segment, then
					// continue building segments.
					//
					if (id == null) {
						builder.append(
								container.eURIFragmentSegment(internalEObject.eContainingFeature(), internalEObject));

						// We will stop appending segments but will continue the loop for proper
						// isContained checking if there is an ID.
						//
						if (supportIDRelativeURIFragmentPaths) {
							id = getIDForEObject(container);
						}
					}

					internalEObject = container;
					if (container.eDirectResource() == this
							|| unloadingContents != null && unloadingContents.contains(container)) {
						isContained = true;
						break;
					}
				}

				if (!isContained) {
					return "/-1";
				}

				builder.append(id != null ? "?" + id : getURIFragmentRootSegment(internalEObject));
				builder.append("");
				builder.reverse();

				// Note that we convert it to a segment sequence because the most common use
				// case is that callers of this method will call URI.appendFragment.
				// By creating the segment sequence here, we ensure that it's found in the
				// cache.
				//
				return builder.toSegmentSequence().toString();
			}
		}
	}

	/**
	 * Returns whether {@link #getURIFragment(EObject)} should support ID-relative
	 * URI fragment segments. Normally that method returns either the ID of the
	 * object, or a fragment path relative to the root object. When this is enabled,
	 * the fragment path construction stops at the first
	 * {@link #getIDForEObject(EObject) object with an ID} to construct a path of to
	 * form {@code /?<id>/...}
	 * 
	 * @since 2.14
	 */
	protected boolean supportIDRelativeURIFragmentPaths() {
		return false;
	}

	/**
	 * Returns the ID for the given EObject such that
	 * {@link #getEObjectByID(String)} would return this same object. It is used by
	 * {@link #getURIFragment(EObject)}, but only if
	 * {@link #supportIDRelativeURIFragmentPaths()} is {@code true}.
	 *
	 * @since 2.14
	 */
	protected String getIDForEObject(EObject eObject) {
		return EcoreUtil.getID(eObject);
	}

	/**
	 * Returns the object associated with the URI fragment root segment. This
	 * default implementation uses the position of the object; an empty string is
	 * the same as <code>"0"</code>.
	 * 
	 * @return the object associated with the URI fragment root segment.
	 */
	protected EObject getEObjectForURIFragmentRootSegment(String uriFragmentRootSegment) {
		int position = 0;
		if (uriFragmentRootSegment.length() > 0) {
			if (uriFragmentRootSegment.charAt(0) == '?') {
				return getEObjectByID(uriFragmentRootSegment.substring(1));
			} else {
				try {
					position = Integer.parseInt(uriFragmentRootSegment);
				} catch (NumberFormatException exception) {
					throw new WrappedException(exception);
				}
			}
		}

		List<EObject> contents = getContents();
		if (position < contents.size() && position >= 0) {
			return contents.get(position);
		} else {
			return null;
		}
	}

	/*
	 * Javadoc copied from interface.
	 */
	public EObject getEObject(String uriFragment) {
		int length = uriFragment.length();
		if (length > 0) {
			if (uriFragment.charAt(0) == '/') {
				return getEObject(SegmentSequence.create("/", uriFragment).subSegmentsList(1));
			} else if (uriFragment.charAt(length - 1) == '?') {
				int index = uriFragment.lastIndexOf('?', length - 2);
				if (index > 0) {
					uriFragment = uriFragment.substring(0, index);
				}
			}
		}

		return getEObjectByID(uriFragment);
	}

	/**
	 * Returns the object based on the fragment path as a list of Strings.
	 */
	protected EObject getEObject(List<String> uriFragmentPath) {
		int size = uriFragmentPath.size();
		EObject eObject = getEObjectForURIFragmentRootSegment(size == 0 ? "" : uriFragmentPath.get(0));
		for (int i = 1; i < size && eObject != null; ++i) {
			eObject = ((InternalEObject) eObject).eObjectForURIFragmentSegment(uriFragmentPath.get(i));
		}

		return eObject;
	}

	/**
	 * Returns the map used to cache the EObject that is identified by the
	 * {@link #getEObjectByID(String) value} of its ID feature.
	 * 
	 * @return the map used to cache the EObject that is identified by the value of
	 *         its ID feature.
	 * @see #setIntrinsicIDToEObjectMap
	 */
	public Map<String, EObject> getIntrinsicIDToEObjectMap() {
		return intrinsicIDToEObjectMap;
	}

	/**
	 * Sets the map used to cache the EObject identified by the value of its ID
	 * feature. This cache is only activated if the map is not <code>null</code>.
	 * The map will be lazily loaded by the {@link #getEObjectByID(String)
	 * getEObjectByID} method. It is up to the client to clear the cache when it
	 * becomes invalid, e.g., when the ID of a previously mapped EObject is changed.
	 * 
	 * @param intrinsicIDToEObjectMap the new map or <code>null</code>.
	 * @see #getIntrinsicIDToEObjectMap
	 */
	public void setIntrinsicIDToEObjectMap(Map<String, EObject> intrinsicIDToEObjectMap) {
		this.intrinsicIDToEObjectMap = intrinsicIDToEObjectMap;
	}

	/**
	 * Returns the object based on the fragment as an ID.
	 */
	protected EObject getEObjectByID(String id) {
		Map<String, EObject> map = getIntrinsicIDToEObjectMap();
		if (map != null) {
			EObject eObject = map.get(id);
			if (eObject != null) {
				return eObject;
			}
		}

		EObject result = null;
		for (TreeIterator<EObject> i = getAllProperContents(getContents()); i.hasNext();) {
			EObject eObject = i.next();
			String eObjectId = EcoreUtil.getID(eObject);
			if (eObjectId != null) {
				if (map != null) {
					map.put(eObjectId, eObject);
				}

				if (eObjectId.equals(id)) {
					result = eObject;
					if (map == null) {
						break;
					}
				}
			}
		}

		return result;
	}

	public void attached(EObject eObject) {
		if (isAttachedDetachedHelperRequired()) {
			attachedHelper(eObject);
			for (TreeIterator<EObject> tree = getAllProperContents(eObject); tree.hasNext();) {
				attachedHelper(tree.next());
			}
		}
	}

	protected boolean isAttachedDetachedHelperRequired() {
		return isTrackingModification() || getIntrinsicIDToEObjectMap() != null;
	}

	protected void attachedHelper(EObject eObject) {
		if (isTrackingModification()) {
			eObject.eAdapters().add(modificationTrackingAdapter);
		}

		Map<String, EObject> map = getIntrinsicIDToEObjectMap();
		if (map != null) {
			String id = EcoreUtil.getID(eObject);
			if (id != null) {
				map.put(id, eObject);
			}
		}
	}

	public void detached(EObject eObject) {
		if (isAttachedDetachedHelperRequired()) {
			detachedHelper(eObject);
			for (TreeIterator<EObject> tree = getAllProperContents(eObject); tree.hasNext();) {
				detachedHelper(tree.next());
			}
		}
	}

	protected void detachedHelper(EObject eObject) {
		Map<String, EObject> map = getIntrinsicIDToEObjectMap();
		if (map != null) {
			String id = EcoreUtil.getID(eObject);
			if (id != null) {
				map.remove(id);
			}
		}

		if (isTrackingModification()) {
			eObject.eAdapters().remove(modificationTrackingAdapter);
		}
	}

	/**
	 * Returns the URI converter. This typically gets the
	 * {@link ResourceSet#getURIConverter converter} from the {@link #getResourceSet
	 * containing} resource set, but it calls {@link #getDefaultURIConverter} when
	 * there is no containing resource set.
	 * 
	 * @return the URI converter.
	 */
	protected URIConverter getURIConverter() {
		return getResourceSet() == null ? getDefaultURIConverter() : getResourceSet().getURIConverter();
	}

	/**
	 * Returns a new zip entry for {@link #save(OutputStream, Map) saving} the
	 * resource contents. It is called by {@link #save(OutputStream, Map)} when
	 * writing {@link #useZip zipped} contents. This implementation creates an entry
	 * called <code>ResourceContents</code>.
	 * 
	 * @return a new zip entry.
	 * @see #isContentZipEntry(ZipEntry)
	 */
	public ZipEntry newContentZipEntry() {
		return new ZipEntry("ResourceContents");
	}

	/*
	 * Javadoc copied from interface.
	 */
	public boolean isLoaded() {
		return isLoaded;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public boolean isLoading() {
		return isLoading;
	}

	/**
	 * Called when the object is unloaded. This implementation
	 * {@link InternalEObject#eSetProxyURI sets} the object to be a proxy and clears
	 * the {@link #eAdapters adapters}.
	 */
	protected void unloaded(InternalEObject internalEObject) {
		// Ensure that an unresolved containment proxy's URI isn't reset.
		//
		if (!internalEObject.eIsProxy()) {
			internalEObject.eSetProxyURI(uri.appendFragment(getURIFragment(internalEObject)));
		}
		internalEObject.eAdapters().clear();
	}

	/**
	 * Sets the load state as indicated, and returns a notification, if
	 * {@link org.eclipse.emf.common.notify.impl.BasicNotifierImpl#eNotificationRequired
	 * required}. Clients are <b>not</b> expected to call this directly; it is
	 * managed by the implementation.
	 * 
	 * @param isLoaded whether the resource is loaded.
	 * @return a notification.
	 */
	protected Notification setLoaded(boolean isLoaded) {
		boolean oldIsLoaded = this.isLoaded;
		this.isLoaded = isLoaded;

		if (eNotificationRequired()) {
			Notification notification = new NotificationImpl(Notification.SET, oldIsLoaded, isLoaded) {
				@Override
				public Object getNotifier() {
					return null;
				}

				@Override
				public int getFeatureID(Class<?> expectedClass) {
					return RESOURCE__IS_LOADED;
				}
			};
			return notification;
		} else {
			return null;
		}
	}

	/**
	 * This implementation returns a copy of the {@link #getContents() contents}. It
	 * is called by {@link #unload()} to initialize the value of
	 * {@link #unloadingContents}. Clients populating the resource's contents
	 * on-demand can override this implementation to return an empty list when the
	 * resource's contents have not been accessed before the request to unload.
	 * 
	 * @since 2.11
	 */
	protected List<EObject> getUnloadingContents() {
		return new BasicEList.FastCompare<EObject>(getContents());
	}

	/**
	 * Does all the work of unloading the resource. It calls {@link #unloaded
	 * unloaded} for each object it the content {@link #getAllContents tree}, and
	 * clears the {@link #getContents contents}, {@link #getErrors errors}, and
	 * {@link #getWarnings warnings}.
	 */
	protected void doUnload() {
		Iterator<EObject> allContents = getAllProperContents(unloadingContents);

		// This guard is needed to ensure that clear doesn't make the resource become
		// loaded.
		//
		if (!getContents().isEmpty()) {
			getContents().clear();
		}
		getErrors().clear();
		getWarnings().clear();

		while (allContents.hasNext()) {
			unloaded((InternalEObject) allContents.next());
		}
	}

	/*
	 * Javadoc copied from interface.
	 */
	public final void unload() {
		if (isLoaded) {
			unloadingContents = getUnloadingContents();
			Notification notification = setLoaded(false);
			try {
				doUnload();
			} finally {
				unloadingContents = null;
				if (notification != null) {
					eNotify(notification);
				}
				setTimeStamp(URIConverter.NULL_TIME_STAMP);
			}
		}
	}

	/*
	 * Javadoc copied from interface.
	 */
	public boolean isTrackingModification() {
		return modificationTrackingAdapter != null;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public void setTrackingModification(boolean isTrackingModification) {
		boolean oldIsTrackingModification = modificationTrackingAdapter != null;

		if (oldIsTrackingModification != isTrackingModification) {
			if (isTrackingModification) {
				modificationTrackingAdapter = createModificationTrackingAdapter();

				for (TreeIterator<EObject> i = getAllProperContents(getContents()); i.hasNext();) {
					EObject eObject = i.next();
					eObject.eAdapters().add(modificationTrackingAdapter);
				}
			} else {
				Adapter oldModificationTrackingAdapter = modificationTrackingAdapter;
				modificationTrackingAdapter = null;

				for (TreeIterator<EObject> i = getAllProperContents(getContents()); i.hasNext();) {
					EObject eObject = i.next();
					eObject.eAdapters().remove(oldModificationTrackingAdapter);
				}
			}
		}

		if (eNotificationRequired()) {
			Notification notification = new NotificationImpl(Notification.SET, oldIsTrackingModification,
					isTrackingModification) {

				@Override
				public Object getNotifier() {
					return null;
				}

				@Override
				public int getFeatureID(Class<?> expectedClass) {
					return RESOURCE__IS_TRACKING_MODIFICATION;
				}
			};
			eNotify(notification);
		}
	}

	/**
	 * Creates a modification tracking adapter. This implementation creates a
	 * {@link ResourceImpl.ModificationTrackingAdapter}. Clients may override this
	 * to any adapter.
	 * 
	 * @see #modificationTrackingAdapter
	 * @see #isTrackingModification
	 */
	protected Adapter createModificationTrackingAdapter() {
		return new ModificationTrackingAdapter();
	}

	/*
	 * Javadoc copied from interface.
	 */
	public boolean isModified() {
		return isModified;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public void setModified(boolean isModified) {
		boolean oldIsModified = this.isModified;
		this.isModified = isModified;
		if (eNotificationRequired()) {
			Notification notification = new NotificationImpl(Notification.SET, oldIsModified, isModified) {
				@Override
				public Object getNotifier() {
					return null;
				}

				@Override
				public int getFeatureID(Class<?> expectedClass) {
					return RESOURCE__IS_MODIFIED;
				}
			};
			eNotify(notification);
		}
	}

	/**
	 * If an implementation uses IDs and stores the IDs as part of the resource
	 * rather than as objects, this method should return a string representation of
	 * the ID to object mapping, which might be implemented as a Java Map.
	 * 
	 * @return a string representation of the ID to object mapping
	 */
	public String toKeyString() {
		StringBuilder result = new StringBuilder("Key type: ");
		result.append(getClass().toString());
		return result.toString();
	}

	@Override
	public String toString() {
		return getClass().getName() + '@' + Integer.toHexString(hashCode()) + " uri='" + uri + "'";
	}

	/**
	 * An adapter implementation for tracking resource modification.
	 */
	protected class ModificationTrackingAdapter extends AdapterImpl {
		@Override
		public void notifyChanged(Notification notification) {
			if (!notification.isTouch()) {
				setModified(true);
			}
		}
	}

}
