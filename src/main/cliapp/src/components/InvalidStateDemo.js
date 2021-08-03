import React, { useState, useEffect } from 'react';
import { InputText } from 'primereact/inputtext';
import { InputTextarea } from 'primereact/inputtextarea';
import { InputMask } from 'primereact/inputmask';
import { InputNumber } from 'primereact/inputnumber';
import { AutoComplete } from 'primereact/autocomplete';
import { Calendar } from 'primereact/calendar';
import { Chips } from 'primereact/chips';
import { Dropdown } from 'primereact/dropdown';
import { MultiSelect } from 'primereact/multiselect';
import { CountryService } from '../service/CountryService';

export const InvalidStateDemo = () => {

    const [countries, setCountries] = useState([]);
    const [filteredCountries, setFilteredCountries] = useState(null);
    const [value1, setValue1] = useState('');
    const [value2, setValue2] = useState('');
    const [value3, setValue3] = useState(null);
    const [value4, setValue4] = useState('');
    const [value5, setValue5] = useState('');
    const [value6, setValue6] = useState('');
    const [value7, setValue7] = useState('');
    const [value8, setValue8] = useState([]);
    const [value9, setValue9] = useState(null);

    const cities = [
        { name: 'New York', code: 'NY' },
        { name: 'Rome', code: 'RM' },
        { name: 'London', code: 'LDN' },
        { name: 'Istanbul', code: 'IST' },
        { name: 'Paris', code: 'PRS' },
    ];

    useEffect(() => {
        const countryService = new CountryService();
        countryService.getCountries().then((countries) => {
            setCountries(countries);
        });
    }, []);

    const searchCountry = (event) => {
        // in a real application, make a request to a remote url with the query and
        // return filtered results, for demo we filter at client side
        const filtered = [];
        const query = event.query;
        for (let i = 0; i < countries.length; i++) {
            const country = countries[i];
            if (country.name.toLowerCase().indexOf(query.toLowerCase()) === 0) {
                filtered.push(country);
            }
        }
        setFilteredCountries(filtered);
    };

    return (
        <div className="p-grid floatlabel-demo">
            <div className="p-col-12">
                <div className="card p-fluid">
                    <h5>Invalid State</h5>
                    <p>All form components have an invalid state style to display the validation errors.</p>
                    <div className="p-grid">
                        <div className="p-col-12 p-md-6">
                            <div className="p-field">
                                <label htmlFor="inputtext">InputText</label>
                                <InputText type="text" id="inputtext" value={value1} onChange={(e) => setValue1(e.target.value)} className="p-invalid" />
                            </div>
                            <div className="p-field">
                                <label htmlFor="autocomplete">AutoComplete</label>
                                <AutoComplete id="autocomplete" value={value5} onChange={(e) => setValue5(e.value)} suggestions={filteredCountries} completeMethod={searchCountry} field="name" className="p-invalid"></AutoComplete>
                            </div>
                            <div className="p-field">
                                <label htmlFor="calendar">Calendar</label>
                                <Calendar inputId="calendar" value={value6} onChange={(e) => setValue6(e.value)} className="p-invalid"></Calendar>
                            </div>
                            <div className="p-field">
                                <label htmlFor="chips">Chips</label>
                                <Chips inputId="chips" value={value8} onChange={(e) => setValue8(e.value)} className="p-invalid"></Chips>
                            </div>
                            <div className="p-field">
                                <label htmlFor="textarea">Textarea</label>
                                <InputTextarea id="textarea" rows="3" cols="30" value={value4} onChange={(e) => setValue4(e.target.value)} className="p-invalid"></InputTextarea>
                            </div>
                        </div>

                        <div className="p-col-12 p-md-6">
                            <div className="p-field">
                                <label htmlFor="inputmask">InputMask</label>
                                <InputMask id="inputmask" mask="99/99/9999" value={value2} onChange={(e) => setValue2(e.value)} className="p-invalid"></InputMask>
                            </div>
                            <div className="p-field">
                                <label htmlFor="inputnumber">InputNumber</label>
                                <InputNumber id="inputnumber" value={value3} onValueChange={(e) => setValue3(e.target.value)} className="p-invalid"></InputNumber>
                            </div>
                            <div className="p-field">
                                <label htmlFor="dropdown">Dropdown</label>
                                <Dropdown id="dropdown" options={cities} value={value7} onChange={(e) => setValue7(e.value)} optionLabel="name" className="p-invalid"></Dropdown>
                            </div>
                            <div className="p-field">
                                <label htmlFor="multiselect">MultiSelect</label>
                                <MultiSelect id="multiselect" options={cities} value={value9} onChange={(e) => setValue9(e.value)} optionLabel="name" className="p-invalid"></MultiSelect>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}