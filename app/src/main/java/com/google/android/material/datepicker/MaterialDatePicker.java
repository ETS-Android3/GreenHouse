package com.google.android.material.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.dialog.InsetDialogOnTouchListener;
import com.google.android.material.internal.CheckableImageButton;
import com.google.android.material.resources.MaterialAttributes;
import com.google.android.material.shape.MaterialShapeDrawable;
import java.util.Iterator;
import java.util.LinkedHashSet;

/* loaded from: classes.dex */
public final class MaterialDatePicker<S> extends DialogFragment {
    private static final String CALENDAR_CONSTRAINTS_KEY = "CALENDAR_CONSTRAINTS_KEY";
    private static final String DATE_SELECTOR_KEY = "DATE_SELECTOR_KEY";
    private static final String OVERRIDE_THEME_RES_ID = "OVERRIDE_THEME_RES_ID";
    private static final String TITLE_TEXT_KEY = "TITLE_TEXT_KEY";
    private static final String TITLE_TEXT_RES_ID_KEY = "TITLE_TEXT_RES_ID_KEY";
    private MaterialShapeDrawable background;
    private MaterialCalendar<S> calendar;
    private CalendarConstraints calendarConstraints;
    private Button confirmButton;
    private DateSelector<S> dateSelector;
    private boolean fullscreen;
    private TextView headerSelectionText;
    private CheckableImageButton headerToggleButton;
    private int overrideThemeResId;
    private PickerFragment<S> pickerFragment;
    private CharSequence titleText;
    private int titleTextResId;
    static final Object CONFIRM_BUTTON_TAG = "CONFIRM_BUTTON_TAG";
    static final Object CANCEL_BUTTON_TAG = "CANCEL_BUTTON_TAG";
    static final Object TOGGLE_BUTTON_TAG = "TOGGLE_BUTTON_TAG";
    private final LinkedHashSet<MaterialPickerOnPositiveButtonClickListener<? super S>> onPositiveButtonClickListeners = new LinkedHashSet<>();
    private final LinkedHashSet<View.OnClickListener> onNegativeButtonClickListeners = new LinkedHashSet<>();
    private final LinkedHashSet<DialogInterface.OnCancelListener> onCancelListeners = new LinkedHashSet<>();
    private final LinkedHashSet<DialogInterface.OnDismissListener> onDismissListeners = new LinkedHashSet<>();

    public static long todayInUtcMilliseconds() {
        return UtcDates.getTodayCalendar().getTimeInMillis();
    }

    public static long thisMonthInUtcMilliseconds() {
        return Month.today().timeInMillis;
    }

    public String getHeaderText() {
        return this.dateSelector.getSelectionDisplayString(getContext());
    }

    static <S> MaterialDatePicker<S> newInstance(Builder<S> options) {
        MaterialDatePicker<S> materialDatePickerDialogFragment = new MaterialDatePicker<>();
        Bundle args = new Bundle();
        args.putInt(OVERRIDE_THEME_RES_ID, options.overrideThemeResId);
        args.putParcelable(DATE_SELECTOR_KEY, options.dateSelector);
        args.putParcelable(CALENDAR_CONSTRAINTS_KEY, options.calendarConstraints);
        args.putInt(TITLE_TEXT_RES_ID_KEY, options.titleTextResId);
        args.putCharSequence(TITLE_TEXT_KEY, options.titleText);
        materialDatePickerDialogFragment.setArguments(args);
        return materialDatePickerDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(OVERRIDE_THEME_RES_ID, this.overrideThemeResId);
        bundle.putParcelable(DATE_SELECTOR_KEY, this.dateSelector);
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder(this.calendarConstraints);
        if (this.calendar.getCurrentMonth() != null) {
            constraintsBuilder.setOpenAt(this.calendar.getCurrentMonth().timeInMillis);
        }
        bundle.putParcelable(CALENDAR_CONSTRAINTS_KEY, constraintsBuilder.build());
        bundle.putInt(TITLE_TEXT_RES_ID_KEY, this.titleTextResId);
        bundle.putCharSequence(TITLE_TEXT_KEY, this.titleText);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle activeBundle = bundle == null ? getArguments() : bundle;
        this.overrideThemeResId = activeBundle.getInt(OVERRIDE_THEME_RES_ID);
        this.dateSelector = (DateSelector) activeBundle.getParcelable(DATE_SELECTOR_KEY);
        this.calendarConstraints = (CalendarConstraints) activeBundle.getParcelable(CALENDAR_CONSTRAINTS_KEY);
        this.titleTextResId = activeBundle.getInt(TITLE_TEXT_RES_ID_KEY);
        this.titleText = activeBundle.getCharSequence(TITLE_TEXT_KEY);
    }

    private int getThemeResId(Context context) {
        int i = this.overrideThemeResId;
        if (i != 0) {
            return i;
        }
        return this.dateSelector.getDefaultThemeResId(context);
    }

    @Override // androidx.fragment.app.DialogFragment
    public final Dialog onCreateDialog(Bundle bundle) {
        Dialog dialog = new Dialog(requireContext(), getThemeResId(requireContext()));
        Context context = dialog.getContext();
        this.fullscreen = isFullscreen(context);
        int surfaceColor = MaterialAttributes.resolveOrThrow(context, R.attr.colorSurface, MaterialDatePicker.class.getCanonicalName());
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(context, null, R.attr.materialCalendarStyle, R.style.Widget_MaterialComponents_MaterialCalendar);
        this.background = materialShapeDrawable;
        materialShapeDrawable.initializeElevationOverlay(context);
        this.background.setFillColor(ColorStateList.valueOf(surfaceColor));
        this.background.setElevation(ViewCompat.getElevation(dialog.getWindow().getDecorView()));
        return dialog;
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int layout = this.fullscreen ? R.layout.mtrl_picker_fullscreen : R.layout.mtrl_picker_dialog;
        View root = layoutInflater.inflate(layout, viewGroup);
        Context context = root.getContext();
        if (this.fullscreen) {
            View frame = root.findViewById(R.id.mtrl_calendar_frame);
            frame.setLayoutParams(new LinearLayout.LayoutParams(getPaddedPickerWidth(context), -2));
        } else {
            View pane = root.findViewById(R.id.mtrl_calendar_main_pane);
            View frame2 = root.findViewById(R.id.mtrl_calendar_frame);
            pane.setLayoutParams(new LinearLayout.LayoutParams(getPaddedPickerWidth(context), -1));
            frame2.setMinimumHeight(getDialogPickerHeight(requireContext()));
        }
        TextView textView = (TextView) root.findViewById(R.id.mtrl_picker_header_selection_text);
        this.headerSelectionText = textView;
        ViewCompat.setAccessibilityLiveRegion(textView, 1);
        this.headerToggleButton = (CheckableImageButton) root.findViewById(R.id.mtrl_picker_header_toggle);
        TextView titleTextView = (TextView) root.findViewById(R.id.mtrl_picker_title_text);
        CharSequence charSequence = this.titleText;
        if (charSequence != null) {
            titleTextView.setText(charSequence);
        } else {
            titleTextView.setText(this.titleTextResId);
        }
        initHeaderToggle(context);
        this.confirmButton = (Button) root.findViewById(R.id.confirm_button);
        if (this.dateSelector.isSelectionComplete()) {
            this.confirmButton.setEnabled(true);
        } else {
            this.confirmButton.setEnabled(false);
        }
        this.confirmButton.setTag(CONFIRM_BUTTON_TAG);
        this.confirmButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialDatePicker.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Iterator it = MaterialDatePicker.this.onPositiveButtonClickListeners.iterator();
                while (it.hasNext()) {
                    MaterialPickerOnPositiveButtonClickListener<? super S> listener = (MaterialPickerOnPositiveButtonClickListener) it.next();
                    listener.onPositiveButtonClick(MaterialDatePicker.this.getSelection());
                }
                MaterialDatePicker.this.dismiss();
            }
        });
        Button cancelButton = (Button) root.findViewById(R.id.cancel_button);
        cancelButton.setTag(CANCEL_BUTTON_TAG);
        cancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialDatePicker.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Iterator it = MaterialDatePicker.this.onNegativeButtonClickListeners.iterator();
                while (it.hasNext()) {
                    View.OnClickListener listener = (View.OnClickListener) it.next();
                    listener.onClick(v);
                }
                MaterialDatePicker.this.dismiss();
            }
        });
        return root;
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Window window = requireDialog().getWindow();
        if (this.fullscreen) {
            window.setLayout(-1, -1);
            window.setBackgroundDrawable(this.background);
        } else {
            window.setLayout(-2, -2);
            int inset = getResources().getDimensionPixelOffset(R.dimen.mtrl_calendar_dialog_background_inset);
            Rect insets = new Rect(inset, inset, inset, inset);
            window.setBackgroundDrawable(new InsetDrawable((Drawable) this.background, inset, inset, inset, inset));
            window.getDecorView().setOnTouchListener(new InsetDialogOnTouchListener(requireDialog(), insets));
        }
        startPickerFragment();
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        this.pickerFragment.clearOnSelectionChangedListeners();
        super.onStop();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public final void onCancel(DialogInterface dialogInterface) {
        Iterator<DialogInterface.OnCancelListener> it = this.onCancelListeners.iterator();
        while (it.hasNext()) {
            DialogInterface.OnCancelListener listener = it.next();
            listener.onCancel(dialogInterface);
        }
        super.onCancel(dialogInterface);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public final void onDismiss(DialogInterface dialogInterface) {
        Iterator<DialogInterface.OnDismissListener> it = this.onDismissListeners.iterator();
        while (it.hasNext()) {
            DialogInterface.OnDismissListener listener = it.next();
            listener.onDismiss(dialogInterface);
        }
        ViewGroup viewGroup = (ViewGroup) getView();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        super.onDismiss(dialogInterface);
    }

    public final S getSelection() {
        return this.dateSelector.getSelection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeader() {
        String headerText = getHeaderText();
        this.headerSelectionText.setContentDescription(String.format(getString(R.string.mtrl_picker_announce_current_selection), headerText));
        this.headerSelectionText.setText(headerText);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPickerFragment() {
        this.calendar = MaterialCalendar.newInstance(this.dateSelector, getThemeResId(requireContext()), this.calendarConstraints);
        this.pickerFragment = this.headerToggleButton.isChecked() ? MaterialTextInputPicker.newInstance(this.dateSelector, this.calendarConstraints) : this.calendar;
        updateHeader();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mtrl_calendar_frame, this.pickerFragment);
        fragmentTransaction.commitNow();
        this.pickerFragment.addOnSelectionChangedListener(new OnSelectionChangedListener<S>() { // from class: com.google.android.material.datepicker.MaterialDatePicker.3
            @Override // com.google.android.material.datepicker.OnSelectionChangedListener
            public void onSelectionChanged(S selection) {
                MaterialDatePicker.this.updateHeader();
                if (MaterialDatePicker.this.dateSelector.isSelectionComplete()) {
                    MaterialDatePicker.this.confirmButton.setEnabled(true);
                } else {
                    MaterialDatePicker.this.confirmButton.setEnabled(false);
                }
            }
        });
    }

    private void initHeaderToggle(Context context) {
        this.headerToggleButton.setTag(TOGGLE_BUTTON_TAG);
        this.headerToggleButton.setImageDrawable(createHeaderToggleDrawable(context));
        ViewCompat.setAccessibilityDelegate(this.headerToggleButton, null);
        updateToggleContentDescription(this.headerToggleButton);
        this.headerToggleButton.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialDatePicker.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MaterialDatePicker.this.headerToggleButton.toggle();
                MaterialDatePicker materialDatePicker = MaterialDatePicker.this;
                materialDatePicker.updateToggleContentDescription(materialDatePicker.headerToggleButton);
                MaterialDatePicker.this.startPickerFragment();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateToggleContentDescription(CheckableImageButton toggle) {
        String contentDescription;
        if (this.headerToggleButton.isChecked()) {
            contentDescription = toggle.getContext().getString(R.string.mtrl_picker_toggle_to_calendar_input_mode);
        } else {
            contentDescription = toggle.getContext().getString(R.string.mtrl_picker_toggle_to_text_input_mode);
        }
        this.headerToggleButton.setContentDescription(contentDescription);
    }

    private static Drawable createHeaderToggleDrawable(Context context) {
        StateListDrawable toggleDrawable = new StateListDrawable();
        toggleDrawable.addState(new int[]{16842912}, AppCompatResources.getDrawable(context, R.drawable.ic_calendar_black_24dp));
        toggleDrawable.addState(new int[0], AppCompatResources.getDrawable(context, R.drawable.ic_edit_black_24dp));
        return toggleDrawable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isFullscreen(Context context) {
        int calendarStyle = MaterialAttributes.resolveOrThrow(context, R.attr.materialCalendarStyle, MaterialCalendar.class.getCanonicalName());
        int[] attrs = {16843277};
        TypedArray a = context.obtainStyledAttributes(calendarStyle, attrs);
        boolean fullscreen = a.getBoolean(0, false);
        a.recycle();
        return fullscreen;
    }

    private static int getDialogPickerHeight(Context context) {
        Resources resources = context.getResources();
        int navigationHeight = resources.getDimensionPixelSize(R.dimen.mtrl_calendar_navigation_height) + resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_navigation_top_padding) + resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_navigation_bottom_padding);
        int daysOfWeekHeight = resources.getDimensionPixelSize(R.dimen.mtrl_calendar_days_of_week_height);
        int calendarHeight = (MonthAdapter.MAXIMUM_WEEKS * resources.getDimensionPixelSize(R.dimen.mtrl_calendar_day_height)) + ((MonthAdapter.MAXIMUM_WEEKS - 1) * resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_month_vertical_padding));
        int calendarPadding = resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_bottom_padding);
        return navigationHeight + daysOfWeekHeight + calendarHeight + calendarPadding;
    }

    private static int getPaddedPickerWidth(Context context) {
        Resources resources = context.getResources();
        int padding = resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_content_padding);
        int daysInWeek = Month.today().daysInWeek;
        int dayWidth = resources.getDimensionPixelSize(R.dimen.mtrl_calendar_day_width);
        int horizontalSpace = resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_month_horizontal_padding);
        return (padding * 2) + (daysInWeek * dayWidth) + ((daysInWeek - 1) * horizontalSpace);
    }

    public boolean addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<? super S> onPositiveButtonClickListener) {
        return this.onPositiveButtonClickListeners.add(onPositiveButtonClickListener);
    }

    public boolean removeOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<? super S> onPositiveButtonClickListener) {
        return this.onPositiveButtonClickListeners.remove(onPositiveButtonClickListener);
    }

    public void clearOnPositiveButtonClickListeners() {
        this.onPositiveButtonClickListeners.clear();
    }

    public boolean addOnNegativeButtonClickListener(View.OnClickListener onNegativeButtonClickListener) {
        return this.onNegativeButtonClickListeners.add(onNegativeButtonClickListener);
    }

    public boolean removeOnNegativeButtonClickListener(View.OnClickListener onNegativeButtonClickListener) {
        return this.onNegativeButtonClickListeners.remove(onNegativeButtonClickListener);
    }

    public void clearOnNegativeButtonClickListeners() {
        this.onNegativeButtonClickListeners.clear();
    }

    public boolean addOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        return this.onCancelListeners.add(onCancelListener);
    }

    public boolean removeOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        return this.onCancelListeners.remove(onCancelListener);
    }

    public void clearOnCancelListeners() {
        this.onCancelListeners.clear();
    }

    public boolean addOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        return this.onDismissListeners.add(onDismissListener);
    }

    public boolean removeOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        return this.onDismissListeners.remove(onDismissListener);
    }

    public void clearOnDismissListeners() {
        this.onDismissListeners.clear();
    }

    /* loaded from: classes.dex */
    public static final class Builder<S> {
        CalendarConstraints calendarConstraints;
        final DateSelector<S> dateSelector;
        int overrideThemeResId = 0;
        int titleTextResId = 0;
        CharSequence titleText = null;
        S selection = null;

        private Builder(DateSelector<S> dateSelector) {
            this.dateSelector = dateSelector;
        }

        static <S> Builder<S> customDatePicker(DateSelector<S> dateSelector) {
            return new Builder<>(dateSelector);
        }

        public static Builder<Long> datePicker() {
            return new Builder<>(new SingleDateSelector());
        }

        public static Builder<Pair<Long, Long>> dateRangePicker() {
            return new Builder<>(new RangeDateSelector());
        }

        public Builder<S> setSelection(S selection) {
            this.selection = selection;
            return this;
        }

        public Builder<S> setTheme(int themeResId) {
            this.overrideThemeResId = themeResId;
            return this;
        }

        public Builder<S> setCalendarConstraints(CalendarConstraints bounds) {
            this.calendarConstraints = bounds;
            return this;
        }

        public Builder<S> setTitleText(int titleTextResId) {
            this.titleTextResId = titleTextResId;
            this.titleText = null;
            return this;
        }

        public Builder<S> setTitleText(CharSequence charSequence) {
            this.titleText = charSequence;
            this.titleTextResId = 0;
            return this;
        }

        public MaterialDatePicker<S> build() {
            if (this.calendarConstraints == null) {
                this.calendarConstraints = new CalendarConstraints.Builder().build();
            }
            if (this.titleTextResId == 0) {
                this.titleTextResId = this.dateSelector.getDefaultTitleResId();
            }
            S s = this.selection;
            if (s != null) {
                this.dateSelector.setSelection(s);
            }
            return MaterialDatePicker.newInstance(this);
        }
    }
}
