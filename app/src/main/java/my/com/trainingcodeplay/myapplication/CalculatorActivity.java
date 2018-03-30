package my.com.trainingcodeplay.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CalculatorActivity extends AppCompatActivity {

    private EditText etLoanAmount, etDownPayment, etTerm, etAnnualInterestRate;
    private TextView tvMonthlyPayment, tvTotalRepayment, tvTotalInterest, tvAverageMonthlyInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        etLoanAmount = (EditText) findViewById(R.id.loan_amount);
        etDownPayment = (EditText) findViewById(R.id.down_payment);
        etTerm = (EditText) findViewById(R.id.term);
        etAnnualInterestRate = (EditText) findViewById(R.id.annual_interest_rate);

        tvMonthlyPayment = (TextView) findViewById(R.id.monthly_repayment);
        tvTotalRepayment = (TextView) findViewById(R.id.total_repayment);
        tvTotalInterest = (TextView) findViewById(R.id.total_interest);
        tvAverageMonthlyInterest = (TextView) findViewById(R.id.average_monthly_interest);

        SharedPreferences sp= getSharedPreferences(PREFS_CALCULATIONS, Context.MODE_PRIVATE);
        if (sp.getBoolean(HAS_RECORD, false)) {
            tvMonthlyPayment.setText(sp.getString(MONTHLY_REPAYMENT,""));
            tvTotalRepayment.setText(sp.getString(TOTAL_REPAYMENT,""));
            tvTotalInterest.setText(sp.getString(TOTAL_INTEREST,""));
            tvAverageMonthlyInterest.setText(sp.getString(MONTHLY_INTEREST,""));
        }
    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.button_calculate:
                Log.d("Check", "Calculate button clicked");
                calculate();
                break;
            case R.id.button_reset:
                Log.d("Check", "Reset button clicked");
                reset();
                break;
        }
    }

    public static final String PREFS_CALCULATIONS = "LoanCalculation";
    public static final String HAS_RECORD = "hasRecord";
    public static final String MONTHLY_REPAYMENT = "monthly repayment";
    public static final String TOTAL_REPAYMENT = "total repayment";
    public static final String TOTAL_INTEREST = "total interest";
    public static final String MONTHLY_INTEREST = "monthly interes";

    private void calculate() {
        String amount = etLoanAmount.getText().toString();
        String downpayment = etDownPayment.getText().toString();
        String interestRate = etAnnualInterestRate.getText().toString();
        String term = etTerm.getText().toString();

        double loanAmount = Double.parseDouble(amount) - Double.parseDouble(downpayment);
        double interest = Double.parseDouble(interestRate) / 12 / 100;
        double noOfMonth = (Integer.parseInt(term)*12);

        if (noOfMonth > 0) {
            double monthlyRepayment = loanAmount * (interest + (interest/(java.lang.Math.pow ((1+interest), noOfMonth)-1)));
            double totalRepayment = monthlyRepayment * noOfMonth;
            double totalInterest = totalRepayment - loanAmount;
            double monthlyInterest = totalInterest / noOfMonth;

            DecimalFormat format = new DecimalFormat("0.##");
            DecimalFormat format2 = new DecimalFormat("0.#");

            String resultMonthlyRepayment = format.format(monthlyRepayment);
            String resultTotalRepayment = format.format(totalRepayment);
            String resultTotalInterest = format.format(totalInterest);
            String resultMonthlyInterest = format.format(monthlyInterest);

            tvMonthlyPayment.setText(resultMonthlyRepayment);
            tvTotalRepayment.setText(resultTotalRepayment);
            tvTotalInterest.setText(resultTotalInterest);
            tvAverageMonthlyInterest.setText(resultMonthlyInterest);

            SharedPreferences sp = getSharedPreferences(PREFS_CALCULATIONS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString (MONTHLY_REPAYMENT, resultMonthlyRepayment);
            editor.putString (TOTAL_REPAYMENT, resultTotalRepayment);
            editor.putString (TOTAL_INTEREST, resultTotalInterest);
            editor.putString (MONTHLY_INTEREST, resultMonthlyInterest);
            editor.putBoolean (HAS_RECORD, true);
            editor.apply();


        }
    }

    private void reset() {
        etLoanAmount.setText ("");
        etDownPayment.setText ("");
        etTerm.setText ("");
        etAnnualInterestRate.setText ("");

        tvMonthlyPayment.setText(R.string.default_result);
        tvTotalRepayment.setText(R.string.default_result);
        tvTotalInterest.setText(R.string.default_result);
        tvAverageMonthlyInterest.setText(R.string.default_result);
    }
}
